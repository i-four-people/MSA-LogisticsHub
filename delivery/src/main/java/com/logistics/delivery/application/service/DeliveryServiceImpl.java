package com.logistics.delivery.application.service;

import com.logistics.delivery.application.common.OrderStatus;
import com.logistics.delivery.application.dto.PageResponse;
import com.logistics.delivery.application.dto.SearchParameter;
import com.logistics.delivery.application.dto.company.CompanyResponse;
import com.logistics.delivery.application.dto.delivery.DeliveryDeleteResponse;
import com.logistics.delivery.application.dto.delivery.DeliveryDetailResponse;
import com.logistics.delivery.application.dto.delivery.DeliveryResponse;
import com.logistics.delivery.application.dto.delivery.DeliveryRouteResponse;
import com.logistics.delivery.application.dto.deliverymanager.DeliveryManagerResponse;
import com.logistics.delivery.application.dto.deliverymanager.DeliveryManagerType;
import com.logistics.delivery.application.dto.event.DeliveryCreateEvent;
import com.logistics.delivery.application.dto.event.consume.OrderCreateConsume;
import com.logistics.delivery.application.dto.hub.HubResponse;
import com.logistics.delivery.application.dto.order.OrderResponse;
import com.logistics.delivery.application.dto.order.OrderStatusRequest;
import com.logistics.delivery.application.util.EventUtil;
import com.logistics.delivery.domain.model.Delivery;
import com.logistics.delivery.domain.model.DeliveryRoute;
import com.logistics.delivery.domain.model.DeliveryStatus;
import com.logistics.delivery.domain.model.RouteStatus;
import com.logistics.delivery.domain.repository.DeliveryRepository;
import com.logistics.delivery.domain.service.DeliveryRouteService;
import com.logistics.delivery.domain.service.DeliveryService;
import com.logistics.delivery.infrastructure.client.CompanyClient;
import com.logistics.delivery.infrastructure.client.DeliveryManagerClient;
import com.logistics.delivery.infrastructure.client.HubClient;
import com.logistics.delivery.infrastructure.client.OrderClient;
import com.logistics.delivery.infrastructure.config.RabbitMQProperties;
import com.logistics.delivery.presentation.auth.AuthContext;
import com.logistics.delivery.presentation.exception.BusinessException;
import com.logistics.delivery.presentation.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryServiceImpl implements DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryRouteService deliveryRouteService;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitProperties;

    private final OrderClient orderClient;
    private final CompanyClient companyClient;
    private final DeliveryManagerClient deliveryManagerClient;
    private final HubClient hubClient;

    @Override
    public boolean isOrderStatusChangeAllowed(UUID deliveryId, OrderStatusRequest request) {

        Delivery findDelivery = deliveryRepository.findById(deliveryId).orElseThrow(
                () -> new BusinessException(ErrorCode.DELIVERY_NOT_FOUND)
        );

        OrderStatus newOrderStatus = request.orderStatus();
        DeliveryStatus deliveryStatus = findDelivery.getStatus();

        return switch (deliveryStatus) {
            case PENDING -> true;
            case IN_TRANSIT -> false; // 배송 중에는 주문 상태 변경 불가
            case AT_HUB, CANCELLED -> newOrderStatus == OrderStatus.CANCELED;
            case OUT_FOR_DELIVERY -> false; // 업체에 배숭 중일 때는 주문 상태 변경 불가
            case DELIVERED -> newOrderStatus == OrderStatus.COMPLETED;
        };

    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DeliveryResponse> getDeliveries(SearchParameter searchParameter) {
        Page<Delivery> deliveries = deliveryRepository.searchDeliveries(searchParameter);

        // 출발 허브, 도착 허브 정보 조회
        List<UUID> originHubIds = deliveries.map(Delivery::getOriginHubId).stream().distinct().toList();
        List<HubResponse> originHubs = hubClient.getHubsToHubIds(originHubIds).getBody().data();

        List<UUID> destinationHubIds = deliveries.map(Delivery::getDestinationHubId).stream().distinct().toList();
        List<HubResponse> destinationHubs = hubClient.getHubsToHubIds(destinationHubIds).getBody().data();

        // 응답값 반환
        Map<UUID, HubResponse> originHubMap = originHubs.stream().collect(Collectors.toMap(HubResponse::id, h -> h));
        Map<UUID, HubResponse> destinationHubMap = destinationHubs.stream().collect(Collectors.toMap(HubResponse::id, h -> h));

        Page<DeliveryResponse> results = deliveries.map(delivery -> {
            HubResponse originHub = originHubMap.get(delivery.getOriginHubId());
            HubResponse destinationHub = destinationHubMap.get(delivery.getDestinationHubId());
            return DeliveryResponse.from(delivery, originHub, destinationHub);
        });

        return PageResponse.of(results);
    }

    @Override
    @Transactional(readOnly = true)
    public DeliveryDetailResponse getDeliveryById(UUID deliveryId) {

        Delivery findDelivery = deliveryRepository.findById(deliveryId).orElseThrow(
                () -> new BusinessException(ErrorCode.DELIVERY_NOT_FOUND)
        );

        return getDeliveryDetailResponse(findDelivery);
    }

    @Override
    public DeliveryDetailResponse updateDeliveryStatus(UUID deliveryId, DeliveryStatus status) {

        Delivery findDelivery = deliveryRepository.findById(deliveryId).orElseThrow(
                () -> new BusinessException(ErrorCode.DELIVERY_NOT_FOUND)
        );

        // DELIVERED, CANCELLED 상태만 수동 변경 가능
        if (status != DeliveryStatus.DELIVERED && status != DeliveryStatus.CANCELLED) {
            throw new BusinessException(ErrorCode.INVALID_DELIVERY_STATUS_CHANGE);
        }

        findDelivery.updateStatus(status);

        log.info("Updated delivery {} to status {}.", deliveryId, status);

        // 출발 허브, 도착 허브 정보 조회
        return getDeliveryDetailResponse(findDelivery);
    }

    private DeliveryDetailResponse getDeliveryDetailResponse(Delivery findDelivery) {

        // 출발 허브, 도착 허브 정보 조회
        HubResponse originHub = hubClient.getHub(findDelivery.getOriginHubId()).getBody().data();
        HubResponse destinationHub = hubClient.getHub(findDelivery.getDestinationHubId()).getBody().data();

        // 배송 이동 경로 조회
        List<DeliveryRoute> deliveryRoutes = deliveryRouteService.getRoutesByDeliveryId(findDelivery.getId());

        List<UUID> startHubIds = deliveryRoutes.stream().map(DeliveryRoute::getStartHubId).distinct().toList();
        List<HubResponse> startHubs = hubClient.getHubsToHubIds(startHubIds).getBody().data();

        List<UUID> endHubIds = deliveryRoutes.stream().map(DeliveryRoute::getEndHubId).distinct().toList();
        List<HubResponse> endHubs = hubClient.getHubsToHubIds(endHubIds).getBody().data();

        // 응답값 반환
        Map<UUID, HubResponse> startHubMap = startHubs.stream().collect(Collectors.toMap(HubResponse::id, h -> h));
        Map<UUID, HubResponse> endHubMap = endHubs.stream().collect(Collectors.toMap(HubResponse::id, h -> h));

        List<DeliveryRouteResponse> deliveryRouteResponses = deliveryRoutes.stream()
                .map(delivery -> {
                    HubResponse startHub = startHubMap.get(delivery.getStartHubId());
                    HubResponse endHub = endHubMap.get(delivery.getEndHubId());
                    return DeliveryRouteResponse.from(delivery, startHub, endHub);
                }).toList();

        return DeliveryDetailResponse.from(findDelivery, originHub, destinationHub, deliveryRouteResponses);
    }

    @Override
    public void createDelivery(OrderCreateConsume consume) {

        // 출발 허브, 도착 허브 조회
        CompanyResponse recipientCompany = companyClient.findCompanyById(consume.recipientCompanyId()).getBody().data(); // 수령 업체
        CompanyResponse supplyCompany = companyClient.findCompanyById(consume.supplyCompanyId()).getBody().data(); // 공급 업체

        // 주문의 배송이 이미 존재하는 경우
        boolean deliveryExists = deliveryRepository.existsByOrderId(consume.orderId());
        if (deliveryExists) {
            throw new BusinessException(ErrorCode.DUPLICATE_DELIVERY);
        }

        // 배송 정보 저장
        Delivery savedDelivery = deliveryRepository.save(Delivery.create(consume, recipientCompany, supplyCompany));

        // 이벤트 생성
        DeliveryCreateEvent event = DeliveryCreateEvent.of(savedDelivery, AuthContext.get());

        // 배송 정보 저장 이벤트 발행
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange().getDelivery(),
                "",
                EventUtil.serializeEvent(event)
        );

        // 배송 경로 생성
        deliveryRouteService.createRoutesForDelivery(savedDelivery);

        // 업체 배송 담당자 배정
        assignCompanyDeliveryManager(savedDelivery);

    }

    // 업체 배송 담당자를 배정하는 메서드
    @Override
    public void assignCompanyDeliveryManager(Delivery delivery) {
        UUID destinationHubId = delivery.getDestinationHubId(); // 목적지 허브 ID

        List<DeliveryManagerResponse> availableManagers = deliveryManagerClient.findAvailableManagers(DeliveryManagerType.COMPANY_PIC)
                .stream()
                .filter(manager -> manager.hubId().equals(destinationHubId))
                .toList();

        // 해당 목적지 허브에 이미 배정된 업체 배송 담당자 목록 조회
        List<Long> assignedManagerIds = deliveryRepository.findActiveDeliveriesByDestinationHubId(
                destinationHubId, List.of(DeliveryStatus.DELIVERED, DeliveryStatus.CANCELLED)
                )
                .stream()
                .map(Delivery::getCompanyDeliveryManagerId)
                .toList();

        // 배정 가능한 담당자 필터링 (이미 배정된 담장자 제외)
        List<DeliveryManagerResponse> filteredManagers = availableManagers.stream()
                .filter(manager -> !assignedManagerIds.contains(manager.id()))
                .sorted(Comparator.comparing(DeliveryManagerResponse::sequence)) // 순번 기준 정렬
                .toList();

        // 배정 가능한 담당자가 없는 경우 처리
        if (filteredManagers.isEmpty()) {
            log.info("All delivery managers for hub {} are currently assigned to active deliveries. Awaiting completion.", destinationHubId);
            return;
        }

        // 순차적으로 담당자 배정
        DeliveryManagerResponse assignedManager = filteredManagers.getFirst();
        delivery.assignDeliveryCompanyManager(assignedManager.id());

        log.info("Assigned delivery manager {} to delivery {} for destination hub {}",
                assignedManager.id(), delivery.getId(), destinationHubId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Delivery> findUnassignedDeliveries() {
        return deliveryRepository.findUnassignedDeliveries();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Delivery> findAllByStatusNotIn(List<DeliveryStatus> statusList) {
        return deliveryRepository.findAllByStatusNotIn(statusList);
    }

    @Override
    public void updateStatusForDelivery(Delivery delivery) {
        List<DeliveryRoute> routes = deliveryRouteService.getRoutesByDeliveryId(delivery.getId());
        if (routes.isEmpty()) {
            log.warn("Delivery {} has no routes.", delivery.getId());
            return;
        }

        // 모든 경로가 AT_HUB인 경우 배송 상태를 AT_HUB로 변경
        boolean allAtHub = routes.stream().allMatch(route -> route.getStatus() == RouteStatus.AT_HUB);
        if (allAtHub) {
            delivery.updateStatus(DeliveryStatus.AT_HUB);
            log.info("Updated delivery {} to status AT_HUB.", delivery.getId());
            return;
        }

        // 적어도 하나의 경로가 IN_TRANSIT인 경우 배송 상태를 IN_TRANSIT로 변경
        boolean anyInTransit = routes.stream().anyMatch(route -> route.getStatus() == RouteStatus.IN_TRANSIT);
        if (anyInTransit) {
            delivery.updateStatus(DeliveryStatus.IN_TRANSIT);
            log.info("Updated delivery {} to status IN_TRANSIT.", delivery.getId());
            return;
        }

        log.info("No status update for delivery {}. Current status: {}", delivery.getId(), delivery.getStatus());
    }

    @Override
    public DeliveryDeleteResponse deleteDeliveryById(UUID deliveryId) {

        Delivery findDelivery = deliveryRepository.findById(deliveryId).orElseThrow(
                () -> new BusinessException(ErrorCode.DELIVERY_NOT_FOUND)
        );

        OrderResponse findOrder = orderClient.getOrderById(findDelivery.getOrderId()).getBody().data();
        if (findOrder != null) {
            throw new BusinessException(ErrorCode.DELIVERY_DEPENDENT_ORDER_EXISTS);
        }

        // 배송 경로 삭제
        deliveryRouteService.deleteByDeliveryId(findDelivery.getId());

        // TODO: 삭제자 가져오기
        findDelivery.delete("user1");

        return DeliveryDeleteResponse.from(findDelivery);
    }
}
