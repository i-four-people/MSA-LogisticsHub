package com.logistcshub.hub.hub.application.service;

import static com.logistcshub.hub.common.exception.application.type.ErrorCode.AREA_NOT_FOUND;
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
import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub.domain.repository.HubRepository;
import com.logistcshub.hub.hub.presentation.request.AddHubRequestDto;
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

    public AddHubResponseDto addHub(UUID userId, String role, AddHubRequestDto request) {
        String[] addresses = request.address().split(" ");

        State state = State.findState(addresses[0]);
        log.info("state : {}", state.getKoreanName() );
        log.info("city : {}", addresses[1]);
        City city = City.findCity(addresses[1], state);

        StringBuilder sb = new StringBuilder();

        for(int i=2; i<addresses.length; i++) {
            sb.append(addresses[i]).append(" ");
        }

        String detailAddress = sb.toString();

        Area area = areaRepository.findByStateAndCity(state, city).orElseThrow(() ->
                new RestApiException(AREA_NOT_FOUND));

        Hub hub = extracted(request, detailAddress, area);

        hub.create(userId);

        return AddHubResponseDto.of(hubRepository.save(hub));
    }

    private Hub extracted(AddHubRequestDto request, String detailAddress, Area area) {

        try {
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(this.getHeader());

            UriComponents uriComponents = UriComponentsBuilder.fromUriString(kakaoMapConfig.getMapUrl())
                    .queryParam("analyze_type", "similar")
                    .queryParam("page", "1")
                    .queryParam("size", "10")
                    .queryParam("query", request.address())
                    .encode(StandardCharsets.UTF_8) // UTF-8로 인코딩
                    .build();

            URI targetUrl = uriComponents.toUri();
            ResponseEntity<Map> responseEntity = restTemplate.exchange(targetUrl, HttpMethod.GET, requestEntity, Map.class);
            KakaoMapResponseDto kakaoMapResponse = new KakaoMapResponseDto((ArrayList)responseEntity.getBody().get("documents"));

            return Hub.builder()
                        .name(request.name())
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
