package com.logistics.order.application.service;

import com.logistics.order.application.dto.PageResponse;
import com.logistics.order.application.dto.SearchParameter;
import com.logistics.order.application.dto.company.CompanyResponse;
import com.logistics.order.application.dto.event.OrderCreateEvent;
import com.logistics.order.application.dto.event.OrderDeleteEvent;
import com.logistics.order.application.dto.event.consume.DeliveryCreateConsume;
import com.logistics.order.application.dto.order.*;
import com.logistics.order.application.dto.product.ProductIdsResponse;
import com.logistics.order.application.dto.product.ProductResponse;
import com.logistics.order.application.util.EventUtil;
import com.logistics.order.domain.model.Order;
import com.logistics.order.domain.model.OrderStatus;
import com.logistics.order.domain.repository.OrderRepository;
import com.logistics.order.domain.service.OrderService;
import com.logistics.order.infrastructure.client.CompanyClient;
import com.logistics.order.infrastructure.client.DeliveryClient;
import com.logistics.order.infrastructure.client.ProductClient;
import com.logistics.order.infrastructure.config.RabbitMQProperties;
import com.logistics.order.presentation.auth.AuthContext;
import com.logistics.order.presentation.exception.BusinessException;
import com.logistics.order.presentation.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQProperties rabbitProperties;

    private final ProductClient productClient;
    private final CompanyClient companyClient;
    private final DeliveryClient deliveryClient;

    @Override
    public void createOrder(OrderCreateRequest request) {

        // 상품 수량 확인
        ProductResponse product = productClient.findProductById(request.productId()).getBody().data();
        if (product.stock() < request.quantity()) {
            throw new BusinessException(ErrorCode.OUT_OF_STOCK);
        }

        // 주문 정보 저장
        Order savedOrder = orderRepository.save(Order.create(request, product.companyId()));

        // 이벤트 생성
        OrderCreateEvent event = OrderCreateEvent.of(savedOrder, request, AuthContext.get());

        // 이벤트 발행
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange().getOrder(),
                "",
                EventUtil.serializeEvent(event)
        );

        log.info("주문 생성 이벤트 발행 orderId: {}", event.orderId());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getOrders(SearchParameter searchParameter) {
        Page<Order> orders = findOrders(searchParameter);

        // 업체 정보 및 상품 정보 조회
        List<UUID> recipientCompanyIds = orders.map(Order::getRecipientCompanyId).stream().distinct().toList();
        List<CompanyResponse> recipientCompanies = companyClient.findCompaniesByIds(recipientCompanyIds);

        List<UUID> requestCompanyIds = orders.map(Order::getSupplyCompanyId).stream().distinct().toList();
        List<CompanyResponse> requestCompanies = companyClient.findCompaniesByIds(requestCompanyIds);

        List<UUID> productIds = orders.map(Order::getProductId).stream().distinct().toList();
        List<ProductIdsResponse> products = productClient.findProductsByIds(productIds);

        // 응답값 반환
        Map<UUID, CompanyResponse> recipientCompanyMap = recipientCompanies.stream().collect(Collectors.toMap(CompanyResponse::companyId, c -> c));
        Map<UUID, CompanyResponse> requestCompanyMap = requestCompanies.stream().collect(Collectors.toMap(CompanyResponse::companyId, c -> c));
        Map<UUID, ProductIdsResponse> productMap = products.stream().collect(Collectors.toMap(ProductIdsResponse::productId, p -> p));

        Page<OrderResponse> results = orders.map(order -> {
            CompanyResponse recipientCompany = recipientCompanyMap.get(order.getRecipientCompanyId());
            CompanyResponse requestCompany = requestCompanyMap.get(order.getSupplyCompanyId());
            ProductIdsResponse product = productMap.get(order.getProductId());
            return OrderResponse.from(order, recipientCompany, requestCompany, product);
        });

        return PageResponse.of(results);
    }

    private Page<Order> findOrders(SearchParameter searchParameter) {
        if ("RECIPIENT_NAME".equals(searchParameter.getSearchType()) || "SUPPLIER_NAME".equals(searchParameter.getSearchType())) {
            // FeignClient로 업체 ID 리스트 조회
            List<CompanyResponse> findCompanies = companyClient.findCompaniesByName(searchParameter.getSearchValue()).getBody().data();
            List<UUID> companyIds = findCompanies.stream().map(CompanyResponse::companyId).toList();

            // 업체 ID 리스트를 조건으로 필터링
            return orderRepository.searchOrdersByCompanyIds(searchParameter, companyIds);
        }
        return orderRepository.searchOrders(searchParameter);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDetailResponse getOrderById(UUID orderId) {

        Order findOrder = orderRepository.findById(orderId).orElseThrow(
                () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)
        );

        // 업체 정보 및 상품 정보 조회
        CompanyResponse recipientCompany = companyClient.findCompanyById(findOrder.getRecipientCompanyId()).getBody().data();
        CompanyResponse requestCompany = companyClient.findCompanyById(findOrder.getSupplyCompanyId()).getBody().data();
        ProductResponse product = productClient.findProductById(findOrder.getProductId()).getBody().data();

        return OrderDetailResponse.from(findOrder, recipientCompany, requestCompany, product);
    }

    @Override
    public OrderDetailResponse updateOrderById(UUID orderId, OrderUpdateRequest request) {

        Order findOrder = orderRepository.findById(orderId).orElseThrow(
                () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)
        );

        // 상품 수량 확인
        ProductResponse product = productClient.findProductById(findOrder.getProductId()).getBody().data();
        if (findOrder.getQuantity() != request.quantity() && product.stock() < request.quantity()) {
            throw new BusinessException(ErrorCode.OUT_OF_STOCK);
        }

        findOrder.update(request);

        // 업체 정보 및 상품 정보 조회
        CompanyResponse recipientCompany = companyClient.findCompanyById(findOrder.getRecipientCompanyId()).getBody().data();
        CompanyResponse requestCompany = companyClient.findCompanyById(findOrder.getSupplyCompanyId()).getBody().data();

        return OrderDetailResponse.from(findOrder, recipientCompany, requestCompany, product);
    }

    @Override
    public OrderStatusResponse updateOrderStatus(UUID orderId, OrderStatus status) {

        Order findOrder = orderRepository.findById(orderId).orElseThrow(
                () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)
        );

        if (findOrder.getStatus() == status) {
            return OrderStatusResponse.from(findOrder);
        }

        // 배송 상태에 따른 주문 상태 변경 가능 여부확인
        boolean orderStatusChangeAllowed = deliveryClient.isOrderStatusChangeAllowed(findOrder.getDeliveryId(), status.toString()).data();
        if (!orderStatusChangeAllowed) {
            throw new BusinessException(ErrorCode.INVALID_STATUS_CHANGE);
        }

        findOrder.updateStatus(status);

        return OrderStatusResponse.from(findOrder);
    }

    @Override
    public OrderDeleteResponse deleteOrderById(UUID orderId) {

        Order findOrder = orderRepository.findById(orderId).orElseThrow(
                () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)
        );

        if (findOrder.isDelete()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        if (findOrder.getStatus() == OrderStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_COMPLETED);
        }

        findOrder.delete();

        // 이벤트 생성
        OrderDeleteEvent event = OrderDeleteEvent.of(findOrder, AuthContext.get());

        // 이벤트 발행
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange().getOrder(),
                "",
                EventUtil.serializeEvent(event)
        );

        log.info("주문 삭제 이벤트 발행 orderId: {}", event.orderId());

        return OrderDeleteResponse.from(findOrder);
    }

    @Override
    public void updateOrderWithDeliveryId(DeliveryCreateConsume deliveryCreateConsume) {

        Order findOrder = orderRepository.findById(deliveryCreateConsume.orderId()).orElseThrow(
                () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)
        );

        if (findOrder.getDeliveryId() != null) {
            throw new BusinessException(ErrorCode.ORDER_ALREADY_DELIVERY);
        }

        findOrder.updateDelivery(deliveryCreateConsume.deliveryId());
    }
}
