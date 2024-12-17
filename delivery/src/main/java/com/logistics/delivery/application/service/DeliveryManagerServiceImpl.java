package com.logistics.delivery.application.service;

import com.logistics.delivery.application.dto.deliverymanager.DeliveryManagerType;
import com.logistics.delivery.application.dto.deliverymanager.DeliveryManagerUpdateRequest;
import com.logistics.delivery.application.dto.event.SlackCreateEvent;
import com.logistics.delivery.application.dto.deliverymanager.DeliveryManagerResponse;
import com.logistics.delivery.domain.model.DeliveryRoute;
import com.logistics.delivery.domain.repository.DeliveryRouteRepository;
import com.logistics.delivery.domain.service.DeliveryManagerService;
import com.logistics.delivery.infrastructure.client.DeliveryManagerClient;
import com.logistics.delivery.infrastructure.config.RabbitMQProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
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

            if (assignedManagerId != null && !hasExceededWaitingTime(routes)) {
                // 담당자가 배정되어 있고 대기 시간이 초과되지 않은 경우, 기존 담당자를 배정
                assignExistingManagerToRoutes(routes, assignedManagerId);
            } else {
                // 기존 담당자가 없거나 대기 시간이 초과된 경우, 새로운 담당자를 배정
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
        });
    }

    private boolean hasExceededWaitingTime(List<DeliveryRoute> routes) {
        return routes.stream().anyMatch(route ->
                Duration.between(route.getCreatedAt(), LocalDateTime.now()).toMinutes() >= maxWaitingTimeMinutes
        );
    }

    private void assignManagerToGroupedRoutes(RouteKey routeKey, List<DeliveryRoute> routes) {
        UUID startHubId = routeKey.startHubId();

        // 허브 배송 담당자 조회
        List<DeliveryManagerResponse> availableManagers = deliveryManagerClient.findAvailableManagers(DeliveryManagerType.HUB_PIC);

        // 배정 가능한 담당자가 없으면 새로운 배송 경로를 PENDING 상태로 유지
        // 스케줄러를 통해 주기적으로 PENDING 상태의 경로를 확인하고, 완료된 담당자가 있는지 확인.
        if (availableManagers.isEmpty()) {
            log.info("No available delivery managers at hub: {}. The route will remain in PENDING state.", startHubId);
            return; // 배송 담당자 배정 생략
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
        List<DeliveryManagerResponse> matchingHubManagers = availableManagers.stream()
                .filter(manager -> manager.hubId().equals(startHubId))
                .toList();

        // 일치하는 담당자가 없으면 모든 담당자를 사용
        List<DeliveryManagerResponse> managersToAssign = new ArrayList<>(matchingHubManagers.isEmpty()
                ? availableManagers
                : matchingHubManagers
        );

        // 순번 기준으로 정렬
        managersToAssign.sort(Comparator.comparing(DeliveryManagerResponse::sequence));

        // 순번 기준으로 라운드 로빈 방식 적용
        AtomicInteger index = new AtomicInteger(0);

        routes.forEach(route -> {
            // 담당자 배정 (순번 기준으로 라운드 로빈)
            DeliveryManagerResponse assignedManager = availableManagers.get(index.getAndIncrement() % availableManagers.size());
            route.assignManager(assignedManager.id());

            log.info("Assigned manager {} to route from {} to {}",
                    assignedManager.id(), route.getStartHubId(), route.getEndHubId());

            // 배송 담당자의 hubId를 출발지 허브로 업데이트
            deliveryManagerClient.updateHubForManager(assignedManager.id(), new DeliveryManagerUpdateRequest(route.getStartHubId()));

            // 이벤트 생성
            SlackCreateEvent event = SlackCreateEvent.of(route, assignedManager);

            // 슬랙 이벤트 발행
            rabbitTemplate.convertAndSend(
                    rabbitProperties.getExchange().getDelivery(),
                    event
            );

        });
    }

    // 경로 키: 출발 허브와 도착 허브를 묶는 키
    private record RouteKey(UUID startHubId, UUID endHubId) {}

}
