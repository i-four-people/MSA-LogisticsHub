package com.logistcshub.hub.hub_transfer.application.service;

import static com.logistcshub.hub.common.exception.application.type.ErrorCode.ALREADY_EXISTS_HUB_TRANSFER;
import static com.logistcshub.hub.common.exception.application.type.ErrorCode.HUB_NOT_FOUND;
import static com.logistcshub.hub.common.exception.application.type.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.logistcshub.hub.common.exception.application.type.ErrorCode.KAKAO_ROAD_CLIENT_ERROR;
import static com.logistcshub.hub.common.exception.application.type.ErrorCode.KAKAO_ROAD_SERVER_ERROR;
import static com.logistcshub.hub.common.exception.application.type.ErrorCode.KAKAO_ROAD_TIME_OUT;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logistcshub.hub.common.config.KakaoRoadApiConfig;
import com.logistcshub.hub.common.domain.model.dtos.KakaoResponse;
import com.logistcshub.hub.common.domain.model.dtos.KakaoResponse.Route.Summary;
import com.logistcshub.hub.common.domain.model.dtos.KakaoRoadResponseDto;
import com.logistcshub.hub.common.exception.RestApiException;
import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub.domain.repository.HubRepository;
import com.logistcshub.hub.hub_transfer.application.dtos.AddHubTransferResponseDto;
import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;
import com.logistcshub.hub.hub_transfer.domain.repository.HubTransferRepository;
import com.logistcshub.hub.hub_transfer.presentation.request.AddHubTransferRequestDto;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
public class HubTransferService {
    private final HubTransferRepository hubTransferRepository;
    private final HubRepository hubRepository;
    private final RestTemplate restTemplate;
    private final KakaoRoadApiConfig kakaoRoadApiConfig;

    @Transactional
    public List<AddHubTransferResponseDto> addHubTransfer(AddHubTransferRequestDto request, String role, Long userId) {

        Hub startHub = hubRepository.findByIdAndDeletedFalse(request.startHubId()).orElseThrow(() ->
                new RestApiException(HUB_NOT_FOUND));

        Hub endHub = hubRepository.findByIdAndDeletedFalse(request.endHubId()).orElseThrow(() ->
                new RestApiException(HUB_NOT_FOUND));

        if(hubTransferRepository.existsByStartHubAndEndHubAndDeletedFalse(startHub, endHub)) {
            throw new RestApiException(ALREADY_EXISTS_HUB_TRANSFER);
        }

        HubTransfer startToEnd = extracted(startHub, endHub);
        HubTransfer endToStart = extracted(endHub, startHub);

        startToEnd = hubTransferRepository.save(startToEnd);
        endToStart = hubTransferRepository.save(endToStart);

        AddHubTransferResponseDto startToEndDto = AddHubTransferResponseDto.of(startToEnd, startHub, endHub);
        AddHubTransferResponseDto endToStartDto = AddHubTransferResponseDto.of(endToStart, endHub, startHub);

        List<AddHubTransferResponseDto> responseDtos = new ArrayList<>();
        responseDtos.add(startToEndDto);
        responseDtos.add(endToStartDto);

        return responseDtos;
    }

    private HubTransfer extracted(Hub startHub, Hub endHub) {
        try {
            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(this.getHeader());

            UriComponents uriComponents = UriComponentsBuilder.fromUriString(kakaoRoadApiConfig.getRoadUrl())
                    .queryParam("origin", startHub.getLng() + "," + startHub.getLat())
                    .queryParam("destination", endHub.getLng() + "," + endHub.getLat())
                    .queryParam("summary", true)
                    .encode(StandardCharsets.UTF_8) // UTF-8로 인코딩
                    .build();

            URI targetUrl = uriComponents.toUri();
            ResponseEntity<Map> responseEntity = restTemplate.exchange(targetUrl, HttpMethod.GET, requestEntity,
                    Map.class);

            ObjectMapper mapper = new ObjectMapper();
            KakaoResponse kakaoResponse = mapper.convertValue(responseEntity.getBody(), KakaoResponse.class);

            Summary summary = kakaoResponse.routes().get(0).summary();
            System.out.println(summary.distance());
            System.out.println(summary.duration());
            KakaoRoadResponseDto kakaoMapResponse = KakaoRoadResponseDto.of(summary);

            return HubTransfer.builder()
                    .startHub(startHub)
                    .endHub(endHub)
                    .distance(kakaoMapResponse.distance()/1000)
                    .timeTaken(kakaoMapResponse.duration()/60)
                    .build();
        } catch (HttpClientErrorException e) {
            // 클라이언트 오류 (4xx)
            throw new RestApiException(KAKAO_ROAD_CLIENT_ERROR);
        } catch (HttpServerErrorException e) {
            // 서버 오류 (5xx)
            throw new RestApiException(KAKAO_ROAD_SERVER_ERROR);
        } catch (RestClientException e) {
            // 일반적인 RestTemplate 오류 처리
            throw new RestApiException(KAKAO_ROAD_TIME_OUT);
        } catch (Exception e) {
            // 기타 예외 처리
            throw new RestApiException(INTERNAL_SERVER_ERROR);
        }
    }

    private HttpHeaders getHeader() {
        HttpHeaders httpHeaders = new HttpHeaders();
        String auth = "KakaoAK " + kakaoRoadApiConfig.getAdminKey();

        httpHeaders.set("Authorization", auth);

        return httpHeaders;
    }
}
