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
import java.util.stream.Collectors;

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

        if (startToEnd != null) {
            return HubToHubResponseDto.of(startToEnd, startHub, endHub);
        }

        List<Hub> hubs = hubRepository.findAll();
        Map<UUID, Integer> hubIndexMap = new HashMap<>();
        Map<UUID, List<HubTransfer>> hubTransfers = new HashMap<>();

        for (int i = 0; i < hubs.size(); i++) {
            hubIndexMap.put(hubs.get(i).getId(), i);
        }

        hubTransferRepository.findByIsDeletedFalse().forEach(transfer -> {
            hubTransfers.computeIfAbsent(transfer.getStartHub().getId(), k -> new ArrayList<>()).add(transfer);
        });

        int[] timeTaken = new int[hubs.size()];
        int[] totalDistance = new int[hubs.size()];
        Arrays.fill(timeTaken, Integer.MAX_VALUE);
        timeTaken[hubIndexMap.get(startHubId)] = 0;

        PriorityQueue<HubPath> pq = new PriorityQueue<>(Comparator.comparingInt(HubPath::getEstimatedCost));
        pq.add(new HubPath(startHubId, List.of(startHubId), new ArrayList<>(), 0, 0, heuristic(startHub, endHub)));

        HubPath shortestPath = null;

        while (!pq.isEmpty()) {
            HubPath currentPath = pq.poll();

            UUID currentHubId = currentPath.getHubId();
            int currentTime = currentPath.getTime();

            if (currentHubId.equals(endHubId)) {
                shortestPath = currentPath;
                break;
            }

            for (HubTransfer transfer : hubTransfers.getOrDefault(currentHubId, Collections.emptyList())) {
                UUID nextHubId = transfer.getEndHub().getId();
                int nextTime = currentTime + transfer.getTimeTaken();

                if (nextTime < timeTaken[hubIndexMap.get(nextHubId)]) {
                    timeTaken[hubIndexMap.get(nextHubId)] = nextTime;
                    totalDistance[hubIndexMap.get(nextHubId)] = currentPath.getDistance() + transfer.getDistance();

                    List<UUID> newHubPath = new ArrayList<>(currentPath.getHubPath());
                    newHubPath.add(nextHubId);

                    List<UUID> newTransferPath = new ArrayList<>(currentPath.getTransferPath());
                    newTransferPath.add(transfer.getId());

                    pq.add(new HubPath(nextHubId, newHubPath, newTransferPath, nextTime, totalDistance[hubIndexMap.get(nextHubId)], heuristic(hubs.get(hubIndexMap.get(nextHubId)), endHub)));
                }
            }
        }

        if (shortestPath == null) {
            throw new RuntimeException("No path found between the hubs.");
        }

        List<HubToHubResponseDto.HubToHub> hubToHubResponseDtoList = shortestPath.getHubPath().stream()
                .skip(1)
                .filter(hubId -> !hubId.equals(endHubId))
                .map(hubId -> HubToHubResponseDto.HubToHub.of(hubs.get(hubIndexMap.get(hubId))))
                .collect(Collectors.toList());

        List<HubToHubResponseDto.HubToHubInfo> hubToHubInfoList = shortestPath.getTransferPath().stream()
                .map(uuid -> HubToHubResponseDto.HubToHubInfo.of(hubTransferRepository.findByIdAndIsDeletedFalse(uuid).orElseThrow()))
                .collect(Collectors.toList());

        return new HubToHubResponseDto(
                HubToHubResponseDto.HubToHub.of(hubs.get(hubIndexMap.get(shortestPath.getHubPath().get(0)))),
                HubToHubResponseDto.HubToHub.of(hubs.get(hubIndexMap.get(shortestPath.getHubPath().get(shortestPath.getHubPath().size() - 1)))),
                hubToHubResponseDtoList,
                timeTaken[hubIndexMap.get(endHubId)],
                totalDistance[hubIndexMap.get(endHubId)],
                hubToHubInfoList
        );
    }

    private int heuristic(Hub currentHub, Hub endHub) {
        double lat1 = Math.toRadians(currentHub.getLat());
        double lng1 = Math.toRadians(currentHub.getLng());
        double lat2 = Math.toRadians(endHub.getLat());
        double lng2 = Math.toRadians(endHub.getLng());

        double dLat = lat2 - lat1;
        double dLng = lng2 - lng1;

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(lat1) * Math.cos(lat2) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double earthRadius = 6371; // Earth's radius in kilometers

        return (int) (earthRadius * c);
    }

    private static class HubPath {
        private final UUID hubId;
        private final List<UUID> hubPath;
        private final List<UUID> transferPath;
        private final int time;
        private final int distance;
        private final int estimatedCost;

        public HubPath(UUID hubId, List<UUID> hubPath, List<UUID> transferPath, int time, int distance, int estimatedCost) {
            this.hubId = hubId;
            this.hubPath = hubPath;
            this.transferPath = transferPath;
            this.time = time;
            this.distance = distance;
            this.estimatedCost = estimatedCost;
        }

        public UUID getHubId() {
            return hubId;
        }

        public List<UUID> getHubPath() {
            return hubPath;
        }

        public List<UUID> getTransferPath() {
            return transferPath;
        }

        public int getTime() {
            return time;
        }

        public int getDistance() {
            return distance;
        }

        public int getEstimatedCost() {
            return estimatedCost;
        }
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
