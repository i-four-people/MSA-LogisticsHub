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
import com.logistcshub.hub.hub_transfer.application.dtos.*;
import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;
import com.logistcshub.hub.hub_transfer.domain.repository.HubTransferRepository;
import com.logistcshub.hub.hub_transfer.presentation.request.AddHubTransferRequestDto;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;

import com.querydsl.core.types.Predicate;

import com.logistcshub.hub.hub_transfer.presentation.request.UpdateTransferRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
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

import static com.logistcshub.hub.common.exception.application.type.ErrorCode.*;

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
        validateMasterOrHubManager(role);

        Hub startHub = getHubById(request.startHubId());

        Hub endHub = getHubById(request.endHubId());

        if(hubTransferRepository.existsByStartHubAndEndHubAndIsDeletedFalse(startHub, endHub)) {
            throw new RestApiException(ALREADY_EXISTS_HUB_TRANSFER);
        }

        HubTransfer startToEnd = extracted(startHub, endHub);
        startToEnd.create(userId);

        startToEnd = hubTransferRepository.save(startToEnd);

        AddHubTransferResponseDto startToEndDto = AddHubTransferResponseDto.of(startToEnd, startHub, endHub);

        List<AddHubTransferResponseDto> responseDtos = new ArrayList<>();
        responseDtos.add(startToEndDto);

        if(!hubTransferRepository.existsByStartHubAndEndHubAndIsDeletedFalse(endHub, startHub)) {
            HubTransfer endToStart = extracted(endHub, startHub);

            endToStart.create(userId);

            endToStart = hubTransferRepository.save(endToStart);

            AddHubTransferResponseDto endToStartDto = AddHubTransferResponseDto.of(endToStart, endHub, startHub);

            responseDtos.add(endToStartDto);
        }

        return responseDtos;
    }


    public UpdateHubTransferResponseDto updateTransfer(UUID id, UpdateTransferRequestDto request, String role, Long userId) {
        validateMasterOrHubManager(role);
        HubTransfer hubTransfer = getHubTransferById(id);

        Hub startHub = getHubById(request.startHubId());

        Hub endHub = getHubById(request.endHubId());

        if(hubTransferRepository.existsByStartHubAndEndHubAndIsDeletedFalse(startHub, endHub)) {
            throw new RestApiException(ALREADY_EXISTS_HUB_TRANSFER);
        }

        HubTransfer updatedHubTransfer = extracted(startHub, endHub);
        hubTransfer.updateHubTransfer(startHub, endHub, updatedHubTransfer.getTimeTaken(), updatedHubTransfer.getDistance(), userId);

        return UpdateHubTransferResponseDto.of(hubTransferRepository.save(hubTransfer), startHub, endHub);

    }

    @Transactional
    public DeleteHubTransferResponseDto deleteHubTransfer(UUID id, String role, Long userId) {
        validateMasterOrHubManager(role);
        HubTransfer hubTransfer = getHubTransferById(id);

        hubTransfer.delete(userId);
        hubTransfer = hubTransferRepository.save(hubTransfer);

        return new DeleteHubTransferResponseDto(hubTransfer.getStartHub().getName() + "에서 " + hubTransfer.getEndHub().getName() + "로 연결된 노선은 삭제되었습니다.");

    }

    @Transactional(readOnly = true)
    public HubTransferResponseDto getHubTransfer(UUID id, String role, Long userId) {
        validateMasterOrHubManager(role);
        return HubTransferResponseDto.of(getHubTransferById(id));
    }

    @Transactional
    public HubTransfer extracted(Hub startHub, Hub endHub) {
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


    private Hub getHubById(UUID hubId) {
        return hubRepository.findByIdAndDeletedFalse(hubId).orElseThrow(() ->
                new RestApiException(HUB_NOT_FOUND));
    }

    private HubTransfer getHubTransferById(UUID id) {
        return hubTransferRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() ->
                new RestApiException(HUB_TRANSFER_NOT_FOUND));
    }



    @Transactional(readOnly = true)
    public HubTransferPageDto searchHubTransfer(List<UUID> idList, Predicate predicate, Pageable pageable, String role, Long userId) {
        validateRole(role);
        return hubTransferRepository.findAll(idList, predicate, pageable);

    }

    @Cacheable(cacheNames = "hubToHub", key = "#startHubId + ':' + #endHubId")
    public HubToHubResponseDto getHubToHub(UUID startHubId, UUID endHubId, String role, Long userId) {
        validateRole(role);
        HubTransfer startToEnd = hubTransferRepository.findByStartHubIdAndEndHubIdAndIsDeletedFalse(startHubId, endHubId).orElse(null);
        Hub startHub = getHubById(startHubId);
        Hub endHub = getHubById(endHubId);

        if(startToEnd != null) {
            return HubToHubResponseDto.of(startToEnd, startHub, endHub);
        }

        List<Hub> hubList = hubRepository.findAll();

        int[] timeTaken = new int[hubList.size()];
        int[] totalDistance = new int[hubList.size()];

        Arrays.fill(timeTaken, Integer.MAX_VALUE);

        Map<UUID, Integer> hubMap = new HashMap<>();
        Map<UUID, List<HubTransfer>> hubTransferMap = new HashMap<>();

        for(int i = 0; i < hubList.size(); i++) {
            hubMap.put(hubList.get(i).getId(), i);
            hubTransferMap.put(hubList.get(i).getId(), new ArrayList<>());
        }

        timeTaken[hubMap.get(startHubId)] = 0;

        List<HubTransfer> hubTransferList = hubTransferRepository.findByIsDeletedFalse();

        Map<UUID, HubTransfer> uuidHubTransferMap = new HashMap<>();

        hubTransferList.forEach(hubTransfer -> {
            hubTransferMap.get(hubTransfer.getStartHub().getId()).add(hubTransfer);
            uuidHubTransferMap.put(hubTransfer.getId(), hubTransfer);
        });

        Queue<List<List<UUID>>> queue = new LinkedList<>();

        List<List<UUID>> list = new ArrayList<>();

        List<List<UUID>> startList = new ArrayList<>();
        List<UUID> start = new ArrayList<>();
        start.add(startHubId);
        startList.add(start);
        startList.add(new ArrayList<>());

        queue.add(startList);

        while(!queue.isEmpty()) {
            List<List<UUID>> uuids = queue.poll();
            List<UUID> hubUUids = uuids.get(0);
            List<UUID> transferUUids = uuids.get(1);

            for(HubTransfer hubTransfer : hubTransferMap.get(hubUUids.get(hubUUids.size()-1))) {
                if(hubTransfer.getTimeTaken() + timeTaken[hubMap.get(hubUUids.get(hubUUids.size()-1))] < timeTaken[hubMap.get(hubTransfer.getEndHub().getId())]) {
                    timeTaken[hubMap.get(hubTransfer.getEndHub().getId())] = hubTransfer.getTimeTaken() + timeTaken[hubMap.get(hubUUids.get(hubUUids.size()-1))];
                    totalDistance[hubMap.get(hubTransfer.getEndHub().getId())] = hubTransfer.getDistance() + totalDistance[hubMap.get(hubUUids.get(hubUUids.size()-1))];
                    List<UUID> newHubList = new ArrayList<>(hubUUids);
                    newHubList.add(hubTransfer.getEndHub().getId());
                    List<UUID> newHubTransferList = new ArrayList<>(transferUUids);
                    newHubTransferList.add(hubTransfer.getId());
                    List<List<UUID>> newUUIDs = new ArrayList<>();
                    newUUIDs.add(newHubList);
                    newUUIDs.add(newHubTransferList);

                    if(hubTransfer.getEndHub().getId().equals(endHubId)) {
                        list = new ArrayList<>(newUUIDs);
                    }

                    queue.add(newUUIDs);
                }
            }
        }

        List<HubToHubResponseDto.HubToHub> hubToHubResponseDtoList = new ArrayList<>();

        for(int i=1; i < list.get(0).size() - 1; i++) {
            hubToHubResponseDtoList.add(
                    HubToHubResponseDto.HubToHub.of(hubList.get(hubMap.get(list.get(0).get(i))))
            );
        }

        List<HubToHubResponseDto.HubToHubInfo> hubToHubInfoList = new ArrayList<>();

        for(int i=0; i < list.get(1).size(); i++) {
            hubToHubInfoList.add(HubToHubResponseDto.HubToHubInfo.of(uuidHubTransferMap.get(list.get(1).get(i))));
        }

        return new HubToHubResponseDto(
                HubToHubResponseDto.HubToHub.of(hubList.get(hubMap.get(startHubId))),
                HubToHubResponseDto.HubToHub.of(hubList.get(hubMap.get(endHubId))),
                hubToHubResponseDtoList,
                timeTaken[hubMap.get(endHubId)],
                totalDistance[hubMap.get(endHubId)],
                hubToHubInfoList
        );
    }

    private void validateMasterOrHubManager(String role) {
        if(role == null || !role.equals("MASTER")) {
            throw new RestApiException(FORBIDDEN);
        }
    }

    private void validateRole(String role) {
        if(role == null || !(role.equals("MASTER") || role.equals("HUB_MANAGER") || role.equals("COMPANY_MANAGER") || role.equals("DELIVERY_MANAGER"))) {
            throw new RestApiException(FORBIDDEN);
        }
    }

}
