package com.logistics.delivery.application.service;

import com.logistics.delivery.application.dto.deliverymanager.DeliveryManagerResponse;
import com.logistics.delivery.application.dto.deliverymanager.DeliveryManagerType;
import com.logistics.delivery.application.dto.deliverymanager.DeliveryManagerUpdateRequest;
import com.logistics.delivery.application.dto.event.SlackCreateEvent;
import com.logistics.delivery.application.util.EventUtil;
import com.logistics.delivery.domain.model.DeliveryRoute;
import com.logistics.delivery.domain.model.RouteStatus;
import com.logistics.delivery.domain.repository.DeliveryRouteRepository;
import com.logistics.delivery.domain.service.DeliveryManagerService;
import com.logistics.delivery.infrastructure.client.DeliveryManagerClient;
import com.logistics.delivery.infrastructure.config.RabbitMQProperties;
import com.logistics.delivery.presentation.auth.AuthContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryManagerServiceImpl implements DeliveryManagerService {

    private final DeliveryRouteRepository deliveryRouteRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitProperties;

    private final DeliveryManagerClient deliveryManagerClient;

    @Value("${delivery.max-waiting-time-minutes}")
    private int maxWaitingTimeMinutes;

    @Override
    public void assignManagersForPendingRoutes() {
        // 대기 중인 배송 경로 가져오기
        List<DeliveryRoute> pendingRoutes = deliveryRouteRepository.findPendingRoutes();

        // 배송 이동 경로를 출발 허브와 도착 허브로 그룹화
        Map<RouteKey, List<DeliveryRoute>> groupedRoutes = pendingRoutes.stream()
                .collect(Collectors.groupingBy(route -> new RouteKey(route.getStartHubId(), route.getEndHubId())));

        // 각 그룹에 대해 배송 담당자 배정 처리
        groupedRoutes.forEach((routeKey, routes) -> {
            // 동일 경로에서 이미 배정된 배송 담당자가 있는지 확인
            Long assignedManagerId = findAssignedManagerForRoute(routeKey);

            // 상태 확인: IN_TRANSIT, AT_HUB 제외
            boolean hasExcludedStatus = routes.stream()
                    .anyMatch(route -> route.getStatus() == RouteStatus.IN_TRANSIT || route.getStatus() == RouteStatus.AT_HUB);

            if (assignedManagerId != null && !hasExcludedStatus) {
                // 담당자가 배정되어 있고 아직 출발하지 않은 경우 기존 담당자를 배정
                assignExistingManagerToRoutes(routes, assignedManagerId);
            } else {
                // 기존 담당자가 없거나 이미 출발했을 경우는 새로운 담당자를 배정
                assignManagerToGroupedRoutes(routeKey, routes);
            }
        });
    }

    // 동일 경로에서 ASSIGNED 상태의 배송 담당자가 있는지 확인
    private Long findAssignedManagerForRoute(RouteKey routeKey) {
        return deliveryRouteRepository.findAssignedManagerByRoute(routeKey.startHubId(), routeKey.endHubId())
                .orElse(null);
    }

    // 기존 배송 담당자를 경로에 배정
    private void assignExistingManagerToRoutes(List<DeliveryRoute> routes, Long assignedManagerId) {
        routes.forEach(route -> {
            route.assignManager(assignedManagerId); // 기존 담당자 배정

            deliveryManagerClient.findAvailableManagers(DeliveryManagerType.HUB_PIC).stream()
                    .filter(manager -> manager.id().equals(assignedManagerId))
                    .findFirst()
                    .ifPresent(assignedManager -> {

                        // 이벤트 생성
                        SlackCreateEvent event = SlackCreateEvent.of(route, assignedManager, AuthContext.get());

                        // 슬랙 이벤트 발행
                        rabbitTemplate.convertAndSend(
                                rabbitProperties.getExchange().getDelivery(),
                                "",
                                EventUtil.serializeEvent(event)
                        );
                    });
        });
    }

    /*private boolean hasExceededWaitingTime(List<DeliveryRoute> routes) {
        return routes.stream().anyMatch(route ->
                Duration.between(route.getCreatedAt(), LocalDateTime.now()).toMinutes() >= maxWaitingTimeMinutes
        );
    }*/

    private void assignManagerToGroupedRoutes(RouteKey routeKey, List<DeliveryRoute> routes) {
        UUID startHubId = routeKey.startHubId();

        // 허브 배송 담당자 조회
        List<DeliveryManagerResponse> availableManagers = deliveryManagerClient.findAvailableManagers(DeliveryManagerType.HUB_PIC);

        // 배정 가능한 담당자가 없으면 새로운 배송 경로를 PENDING 상태로 유지
        // 스케줄러를 통해 주기적으로 PENDING 상태의 경로를 확인하고, 완료된 담당자가 있는지 확인.
        if (availableManagers == null || availableManagers.isEmpty()) {
            log.info("No available delivery managers at hub: {}. The route will remain in PENDING state.", startHubId);
            return;
        }

        // 이미 배정된 담당자 ID 가져오기
        List<Long> assignedManagerIds = deliveryRouteRepository.findAssignedManagerIds();

        // 배정 가능한 담당자 필터링
        List<DeliveryManagerResponse> unassignedManagers = availableManagers.stream()
                .filter(manager -> !assignedManagerIds.contains(manager.id())) // 이미 배정된 담당자는 제외
                .toList();

        if (unassignedManagers.isEmpty()) {
            log.info("All delivery managers are currently assigned. The route will remain in PENDING state.");
            return;
        }

        // 허브 ID가 startHubId와 일치하는 담당자를 먼저 필터링 (현재 허브에 위차한 배송 담당자 먼저 확인)
        Optional<DeliveryManagerResponse> matchingHubManagers = unassignedManagers.stream()
                .filter(manager -> manager.hubId().equals(startHubId))
                .min(Comparator.comparing(DeliveryManagerResponse::sequence)); // sequence 기준으로 최소값 찾기

        DeliveryManagerResponse managerToAssign = matchingHubManagers.orElseGet(
                () -> getDeliveryManagerResponse(routeKey, availableManagers) // 순번을 위한 인덱스 (라운드 로빈)
        );

        // 동일 그룹의 모든 routes에 같은 매니저 배정
        routes.forEach(route -> {
            route.assignManager(managerToAssign.id());

            log.info("Assigned manager {} to route from {} to {}",
                    managerToAssign.id(), route.getStartHubId(), route.getEndHubId());

            // 배송 담당자의 hubId를 출발지 허브로 업데이트
            deliveryManagerClient.updateHubForManager(managerToAssign.id(), new DeliveryManagerUpdateRequest(route.getStartHubId()));

            // 이벤트 생성
            SlackCreateEvent event = SlackCreateEvent.of(route, managerToAssign, AuthContext.get());

            // 슬랙 이벤트 발행
            rabbitTemplate.convertAndSend(
                    rabbitProperties.getExchange().getDelivery(),
                    "",
                    EventUtil.serializeEvent(event)
            );
        });
    }

    private static DeliveryManagerResponse getDeliveryManagerResponse(RouteKey routeKey, List<DeliveryManagerResponse> availableManagers) {
        AtomicInteger roundRobinIndex = new AtomicInteger(0);

        // 이미 배정된 담당자 캐싱 (routeKey -> DeliveryManagerResponse)
        Map<RouteKey, DeliveryManagerResponse> assignedManagerCache = new HashMap<>();

        // 라운드 로빈 방식으로 매니저 선택
        return assignedManagerCache.computeIfAbsent(routeKey, key -> {
            // 새 매니저 선택
            DeliveryManagerResponse assigned = availableManagers.get(roundRobinIndex.getAndIncrement() % availableManagers.size());
            log.info("Selected manager {} for route group: {}", assigned.id(), routeKey);
            return assigned;
        });
    }


    // 경로 키: 출발 허브와 도착 허브를 묶는 키
    private record RouteKey(UUID startHubId, UUID endHubId) {}

}
