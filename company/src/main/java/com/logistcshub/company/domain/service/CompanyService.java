package com.logistcshub.company.domain.service;

import com.logistcshub.company.application.client.HubClient;
import com.logistcshub.company.application.dto.HubResponseDto;
import com.logistcshub.company.domain.model.Company;
import com.logistcshub.company.domain.repository.CompanyRepository;
import com.logistcshub.company.infrastructure.config.RestApiException;
import com.logistcshub.company.presentation.request.CompanyRequestDto;
import com.logistcshub.company.presentation.response.CompanyResponseDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.data.web.PagedModel;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.logistcshub.company.presentation.response.ErrorCode.*;
import static com.logistcshub.company.domain.model.QCompany.company;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final RestTemplate restTemplate;
    private final HubClient hubClient;


    public CompanyResponseDto createCompany(Long userId, String role, CompanyRequestDto companyRequestDto ) {
        log.info("create company 실행");
        Map<String, Object> position = getPosition(companyRequestDto.address());
        System.out.println("=============================="+position);
        HubResponseDto hub = getHub(userId, role, companyRequestDto.address(), position);

        Company company = companyRequestDto.toEntity(Double.valueOf((String) position.get("x")), Double.valueOf((String) position.get("y")), hub.id());
        company.create(userId);
        companyRepository.save(company);

        return CompanyResponseDto.toDto(company);
    }



    public CompanyResponseDto updateCompany(Long userId, String role, CompanyRequestDto companyRequestDto, UUID id) {
        Company company = companyRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("해당 Id값을 갖는 업체가 존재하지 않습니다."));

        Map<String, Object> position = getPosition(companyRequestDto.address());
        HubResponseDto hub = getHub(userId, role, companyRequestDto.address(),position);

        company.update(userId.toString(), companyRequestDto,Double.valueOf((String) position.get("x")), Double.valueOf((String) position.get("y")),hub.id());
        companyRepository.save(company);

        return CompanyResponseDto.toDto(company);
    }

    public CompanyResponseDto deleteCompany(UUID id, Long userId) {
        Company company = companyRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("해당 Id값을 갖는 업체가 존재하지 않습니다."));
        company.delete(userId.toString());
        companyRepository.save(company);

        return CompanyResponseDto.toDto(company);
    }

    @Transactional
    public PagedModel<CompanyResponseDto> getCompanies(Predicate predicate, Pageable pageable) {
        BooleanBuilder booleanBuilder = new BooleanBuilder(predicate);

        booleanBuilder.and(company.isDelete.isFalse());

        Page<Company> companies = companyRepository.findAll(booleanBuilder, pageable);

        return new PagedModel<>(companies.map(CompanyResponseDto::toDto));
    }
    @Transactional(readOnly = true)
    @Cacheable(value = "companies", key = "#id")
    public CompanyResponseDto getCompany(UUID id) {
        Company company = companyRepository.findById(id).orElseThrow(
                () -> new NoSuchElementException("해당 Id값을 갖는 업체가 존재하지 않습니다."));

        return CompanyResponseDto.toDto(company);
    }

    @Value("${api.key}")
    private String apiKey;

    private org.springframework.http.HttpHeaders getHeader() {
        org.springframework.http.HttpHeaders httpHeaders = new HttpHeaders();
        String auth = "KakaoAK " + apiKey;

        httpHeaders.set("Authorization", auth);

        return httpHeaders;
    }

    private Map<String, Object> getPosition(String address) {
        try {
            UriComponents uriComponents = UriComponentsBuilder
                    .fromUriString("https://dapi.kakao.com/v2/local/search/address.json")
                    .queryParam("query", address)
                    .queryParam("analyze_type", "similar")
                    .encode(StandardCharsets.UTF_8)
                    .build();
            URI uri = uriComponents.toUri();
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(this.getHeader());

            List<Double> position = new ArrayList<>();
            ResponseEntity<Map> response = restTemplate.exchange(uri, HttpMethod.GET, requestEntity,Map.class);
            List<Map<String, Object>> documents = (List<Map<String, Object>>) response.getBody().get("documents");
            Map<String, Object> result = documents.get(0);

            return result;
        }catch (HttpClientErrorException e) {
            // 클라이언트 오류 (4xx)
            throw new RestApiException(KAKAO_MAP_CLIENT_ERROR);
        } catch (HttpServerErrorException e) {
            // 서버 오류 (5xx)
            throw new RestApiException(KAKAO_MAP_SERVER_ERROR);
        } catch (RestClientException e) {
            // 일반적인 RestTemplate 오류 처리
            throw new RestApiException(KAKAO_MAP_TIME_OUT);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RestApiException(INTERNAL_SERVER_ERROR);
        }
    }
//    Hub 정보 가져오기
    private HubResponseDto getHub(Long userId, String role, String address, Map<String, Object> position) {

        List<String> splitAddress = List.of(address.split(" "));
        String searchAddress = splitAddress.get(0) + " " + splitAddress.get(1);

        Double lng = null;
        Double lat = null;
        try {
            lng = Double.valueOf((String) position.get("x"));
            lat = Double.valueOf((String) position.get("y"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid coordinates format");
        }
        return hubClient
                .getHubFromCompanyAddress(userId, role, searchAddress, lng, lat)
                .getBody()
                .data();
    }


}
