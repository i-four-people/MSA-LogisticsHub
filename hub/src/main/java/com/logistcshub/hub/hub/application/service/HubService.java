package com.logistcshub.hub.hub.application.service;

import static com.logistcshub.hub.common.exception.application.type.ErrorCode.AREA_NOT_FOUND;
import static com.logistcshub.hub.common.exception.application.type.ErrorCode.HUB_NOT_FOUND;
import static com.logistcshub.hub.common.exception.application.type.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.logistcshub.hub.common.exception.application.type.ErrorCode.KAKAO_MAP_CLIENT_ERROR;
import static com.logistcshub.hub.common.exception.application.type.ErrorCode.KAKAO_MAP_SERVER_ERROR;
import static com.logistcshub.hub.common.exception.application.type.ErrorCode.KAKAO_MAP_TIME_OUT;

import com.logistcshub.hub.area.domain.model.Area;
import com.logistcshub.hub.area.domain.model.type.City;
import com.logistcshub.hub.area.domain.model.type.State;
import com.logistcshub.hub.area.domain.repository.AreaRepository;
import com.logistcshub.hub.common.config.KakaoMapConfig;
import com.logistcshub.hub.common.domain.model.dtos.KakaoMapResponseDto;
import com.logistcshub.hub.common.exception.RestApiException;
import com.logistcshub.hub.hub.application.dtos.AddHubResponseDto;
import com.logistcshub.hub.hub.application.dtos.DeleteHubResponseDto;
import com.logistcshub.hub.hub.application.dtos.HubResponseDto;
import com.logistcshub.hub.hub.application.dtos.UpdateHubResponseDto;
import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub.domain.repository.HubRepository;
import com.logistcshub.hub.hub.presentation.request.AddHubRequestDto;
import com.logistcshub.hub.hub.presentation.request.UpdateHubRequestDto;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
@RequiredArgsConstructor
public class HubService {
    private final HubRepository hubRepository;
    private final AreaRepository areaRepository;
    private final RestTemplate restTemplate;
    private final KakaoMapConfig kakaoMapConfig;

    public AddHubResponseDto addHub(Long userId, String role, AddHubRequestDto request) {
        String[] addresses = request.address().split(" ");

        Area area = findAreaToAddress(addresses);
        String detailAddress = getDetailAddress(addresses);

        Hub hub = extracted(request.name(), request.address(), detailAddress, area);

        hub.create(userId);

        return AddHubResponseDto.of(hubRepository.save(hub));
    }

    @Transactional
    public UpdateHubResponseDto updateHub(UUID id, Long userId, String role, UpdateHubRequestDto request) {
        Hub hub = hubRepository.findByIdWithArea(id).orElseThrow(() ->
                new RestApiException(HUB_NOT_FOUND));
        String[] addresses = request.address().split(" ");

        Area area = findAreaToAddress(addresses);
        String detailAddress = getDetailAddress(addresses);

        if(area.getId().equals(hub.getArea().getId()) && detailAddress.equals(hub.getAddress())) {
            hub.updateName(userId, request.name());
        } else {
            hub.update(userId, extracted(request.name(), request.address(), detailAddress, area));
        }

        return UpdateHubResponseDto.of(hubRepository.save(hub));

    }

    @Transactional
    public DeleteHubResponseDto deleteHub(UUID id, Long userId, String role) {
        Hub hub = hubRepository.findById(id).orElseThrow(() ->
                new RestApiException(HUB_NOT_FOUND));

        hub.delete(userId);

        return DeleteHubResponseDto.of(hubRepository.save(hub));
    }

    public HubResponseDto getHub(UUID id, Long userId, String role) {
        return HubResponseDto.of(hubRepository.findByIdWithArea(id).orElseThrow(() ->
                new RestApiException(HUB_NOT_FOUND)));

    }

    private Area findAreaToAddress(String[] addresses) {
        State state = State.findState(addresses[0]);
        log.info("state : {}", state.getKoreanName());
        City city = City.findCity(addresses[1], state);

        return areaRepository.findByStateAndCity(state, city).orElseThrow(() ->
                new RestApiException(AREA_NOT_FOUND));
    }

    private String getDetailAddress(String[] addresses) {
        StringBuilder sb = new StringBuilder();

        for(int i=2; i<addresses.length; i++) {
            sb.append(addresses[i]).append(" ");
        }

        return sb.toString();
    }

    private Hub extracted(String name, String address, String detailAddress, Area area) {

        try {
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(this.getHeader());

            UriComponents uriComponents = UriComponentsBuilder.fromUriString(kakaoMapConfig.getMapUrl())
                    .queryParam("analyze_type", "similar")
                    .queryParam("page", "1")
                    .queryParam("size", "10")
                    .queryParam("query", address)
                    .encode(StandardCharsets.UTF_8) // UTF-8로 인코딩
                    .build();

            URI targetUrl = uriComponents.toUri();
            ResponseEntity<Map> responseEntity = restTemplate.exchange(targetUrl, HttpMethod.GET, requestEntity,
                    Map.class);
            KakaoMapResponseDto kakaoMapResponse = new KakaoMapResponseDto(
                    (ArrayList) responseEntity.getBody().get("documents"));

            return Hub.builder()
                    .name(name)
                    .address(detailAddress)
                    .lat(Double.parseDouble(kakaoMapResponse.getY()))
                    .lng(Double.parseDouble(kakaoMapResponse.getX()))
                    .area(area)
                    .build();

        } catch (HttpClientErrorException e) {
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

    private HttpHeaders getHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        String auth = "KakaoAK " + kakaoMapConfig.getAdminKey();

        httpHeaders.set("Authorization", auth);

        return httpHeaders;
    }



}
