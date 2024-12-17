package com.logistics.delivery.application.service;

import com.logistics.delivery.application.dto.delivery.DeliveryRouteResponse;
import com.logistics.delivery.application.dto.delivery.RouteStatusUpdateRequest;
import com.logistics.delivery.application.dto.deliverymanager.DeliveryManagerUpdateRequest;
import com.logistics.delivery.application.dto.hub.HubResponse;
import com.logistics.delivery.application.dto.hub.HubToHubResponse;
import com.logistics.delivery.domain.model.Delivery;
import com.logistics.delivery.domain.model.DeliveryRoute;
import com.logistics.delivery.domain.model.RouteStatus;
import com.logistics.delivery.domain.repository.DeliveryRouteRepository;
import com.logistics.delivery.domain.service.DeliveryManagerService;
import com.logistics.delivery.domain.service.DeliveryRouteService;
import com.logistics.delivery.infrastructure.client.DeliveryManagerClient;
import com.logistics.delivery.infrastructure.client.HubClient;
import com.logistics.delivery.presentation.exception.BusinessException;
import com.logistics.delivery.presentation.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryRouteServiceImpl implements DeliveryRouteService {

    private final DeliveryRouteRepository deliveryRouteRepository;
    private final DeliveryManagerService deliveryManagerService;

    private final HubClient hubClient;
    private final DeliveryManagerClient deliveryManagerClient;

    @Override
    public void createRoutesForDelivery(Delivery delivery) {

        // 출발 허브 -> 도착 허브 이동 경로 조회
        HubToHubResponse hubToHubResponse = hubClient.getHubToHubRoutes(delivery.getOriginHubId(), delivery.getDestinationHubId()).getBody().data();

        // 경유지 포함 경로 생성
        List<DeliveryRoute> deliveryRoutes = buildRoutes(delivery, hubToHubResponse);

        // 경로 저장
        deliveryRoutes.forEach(deliveryRouteRepository::save);

        // 배송 담당자 배정
        deliveryManagerService.assignManagersForPendingRoutes();
    }

    private List<DeliveryRoute> buildRoutes(Delivery delivery, HubToHubResponse hubToHubResponse) {

        List<HubToHubResponse.HubDetail> fullRoute = new ArrayList<>();
        fullRoute.add(hubToHubResponse.getStartHub());
        fullRoute.addAll(hubToHubResponse.getStopover());
        fullRoute.add(hubToHubResponse.getEndHub());

        return IntStream.range(0, fullRoute.size() - 1)
                .mapToObj(i -> {
                    HubToHubResponse.HubDetail start = fullRoute.get(i);
                    HubToHubResponse.HubDetail end = fullRoute.get(i + 1);

                    HubToHubResponse.HubToHubInfo info = findHubToHubInfo(hubToHubResponse, start.getHubId(), end.getHubId());

                    int sequence = i + 1;
                    return DeliveryRoute.create(delivery, sequence, start, end, info);
                }).toList();
    }

    private HubToHubResponse.HubToHubInfo findHubToHubInfo(HubToHubResponse hubToHubResponse, UUID startHubId, UUID endHubId) {
        return hubToHubResponse.getHubToHubInfoList().stream()
                .filter(info -> info.getStartHubId().equals(startHubId) && info.getEndHubId().equals(endHubId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No matching HubToHubInfo found for the route."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<DeliveryRoute> getRoutesByDeliveryId(UUID deliveryId) {
        return deliveryRouteRepository.findByDeliveryId(deliveryId);
    }

    @Override
    public DeliveryRouteResponse updateRouteStatus(UUID routeId, RouteStatusUpdateRequest request) {

        DeliveryRoute findDeliveryRoute = deliveryRouteRepository.findById(routeId).orElseThrow(
                () -> new BusinessException(ErrorCode.DELIVERY_ROUTE_NOT_FOUND)
        );

        if (!findDeliveryRoute.getDeliveryManagerId().equals(request.deliveryManagerId())) {
            throw new BusinessException(ErrorCode.MANAGER_NOT_ASSIGNED_TO_ROUTE);
        }

        // 상태 변경 유효성 체크
        validateRouteStatusChange(findDeliveryRoute, request);

        // 상태 업데이트
        findDeliveryRoute.updateStatus(request.status());

        if (request.status() == RouteStatus.IN_TRANSIT) {
            // 출발 상태인 경우
            findDeliveryRoute.recordDepartureTime();
        } else if (request.status() == RouteStatus.AT_HUB) {
            // 실제 소요 시간 계산
            // TODO: 거리 계산
            findDeliveryRoute.recordArrivalTime();
            long duration = findDeliveryRoute.calculateDurationInMinutes();
            log.info("Route {} completed in {} minutes.", routeId, duration);
            findDeliveryRoute.setActualDuration(duration);

            // 도착 상태(AT_HUB)인 경우, 배송 담당자의 위치 업데이트
            deliveryManagerClient.updateHubForManager(request.deliveryManagerId(),
                    new DeliveryManagerUpdateRequest(findDeliveryRoute.getEndHubId()));
        }

        // 출발 허브, 도착 허브 조회
        HubResponse startHub = hubClient.getHub(findDeliveryRoute.getStartHubId()).getBody().data();
        HubResponse endHub = hubClient.getHub(findDeliveryRoute.getEndHubId()).getBody().data();

        return DeliveryRouteResponse.from(findDeliveryRoute, startHub, endHub);
    }

    private void validateRouteStatusChange(DeliveryRoute deliveryRoute, RouteStatusUpdateRequest request) {
        // 기존 상태와 요청 상태 간의 유효성 검사
        RouteStatus currentStatus = deliveryRoute.getStatus();
        RouteStatus newStatus = request.status();

        if (currentStatus == RouteStatus.ASSIGNED && newStatus != RouteStatus.IN_TRANSIT) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_CHANGE_FROM_ASSIGNED);
        }

        if (currentStatus == RouteStatus.IN_TRANSIT && newStatus != RouteStatus.AT_HUB) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_CHANGE_FROM_IN_TRANSIT);
        }

        if (currentStatus == RouteStatus.AT_HUB) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_CHANGE_FROM_AT_HUB);
        }
    }

    @Override
    public void deleteByDeliveryId(UUID deliveryId) {

        List<DeliveryRoute> findDeliveryRoutes = deliveryRouteRepository.findByDeliveryId(deliveryId);
        findDeliveryRoutes.forEach(DeliveryRoute::delete);
    }

}
