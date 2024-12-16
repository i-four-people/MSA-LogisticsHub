package com.logistics.order.application.service;

import com.logistics.order.application.dto.PageResponse;
import com.logistics.order.application.dto.SearchParameter;
import com.logistics.order.application.dto.company.CompanyResponse;
import com.logistics.order.application.dto.event.OrderCreateEvent;
import com.logistics.order.application.dto.event.OrderDeleteEvent;
import com.logistics.order.application.dto.order.*;
import com.logistics.order.application.dto.product.ProductResponse;
import com.logistics.order.domain.model.Order;
import com.logistics.order.domain.model.OrderStatus;
import com.logistics.order.domain.repository.OrderRepository;
import com.logistics.order.domain.service.OrderService;
import com.logistics.order.infrastructure.client.CompanyClient;
import com.logistics.order.infrastructure.client.DeliveryClient;
import com.logistics.order.infrastructure.client.ProductClient;
import com.logistics.order.infrastructure.config.RabbitMQProperties;
import com.logistics.order.presentation.exception.BusinessException;
import com.logistics.order.presentation.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
        ProductResponse product = productClient.findProductById(request.productId());
        if (product.stock() < request.quantity()) {
            throw new BusinessException(ErrorCode.OUT_OF_STOCK);
        }

        // 주문 정보 저장
        Order savedOrder = orderRepository.save(Order.create(request, product.companyId()));

        // 이벤트 생성
        OrderCreateEvent event = OrderCreateEvent.of(savedOrder, request);

        // 이벤트 발행
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange().getOrder(),
                event
        );
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
        List<ProductResponse> products = productClient.findProductsByIds(productIds);

        // 응답값 반환
        Map<UUID, CompanyResponse> recipientCompanyMap = recipientCompanies.stream().collect(Collectors.toMap(CompanyResponse::companyId, c -> c));
        Map<UUID, CompanyResponse> requestCompanyMap = requestCompanies.stream().collect(Collectors.toMap(CompanyResponse::companyId, c -> c));
        Map<UUID, ProductResponse> productMap = products.stream().collect(Collectors.toMap(ProductResponse::productId, p -> p));

        Page<OrderResponse> results = orders.map(order -> {
            CompanyResponse recipientCompany = recipientCompanyMap.get(order.getRecipientCompanyId());
            CompanyResponse requestCompany = requestCompanyMap.get(order.getSupplyCompanyId());
            ProductResponse product = productMap.get(order.getProductId());
            return OrderResponse.from(order, recipientCompany, requestCompany, product);
        });

        return PageResponse.of(results);
    }

    private Page<Order> findOrders(SearchParameter searchParameter) {
        if ("RECIPIENT_NAME".equals(searchParameter.searchType()) || "SUPPLIER_NAME".equals(searchParameter.searchType())) {
            // FeignClient로 업체 ID 리스트 조회
            List<CompanyResponse> findCompanies = companyClient.findCompaniesByName(searchParameter.searchValue());
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
        CompanyResponse recipientCompany = companyClient.findCompanyById(findOrder.getRecipientCompanyId());
        CompanyResponse requestCompany = companyClient.findCompanyById(findOrder.getSupplyCompanyId());
        ProductResponse product = productClient.findProductById(findOrder.getProductId());

        return OrderDetailResponse.from(findOrder, recipientCompany, requestCompany, product);
    }

    @Override
    public OrderDetailResponse updateOrderById(UUID orderId, OrderUpdateRequest request) {

        Order findOrder = orderRepository.findById(orderId).orElseThrow(
                () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)
        );

        // 상품 수량 확인 및 감소
        ProductResponse product = productClient.findProductById(findOrder.getProductId());
        if (findOrder.getQuantity() != request.quantity() && product.stock() < request.quantity()) {
            throw new BusinessException(ErrorCode.OUT_OF_STOCK);
        }

        findOrder.update(request);

        // 업체 정보 및 상품 정보 조회
        CompanyResponse recipientCompany = companyClient.findCompanyById(findOrder.getRecipientCompanyId());
        CompanyResponse requestCompany = companyClient.findCompanyById(findOrder.getSupplyCompanyId());

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
        boolean orderStatusChangeAllowed = deliveryClient.isOrderStatusChangeAllowed(findOrder.getDeliveryId(), status.toString());
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

        // 이벤트 생성
        OrderDeleteEvent event = OrderDeleteEvent.of(findOrder);

        // 이벤트 발행
        rabbitTemplate.convertAndSend(
                rabbitProperties.getExchange().getOrder(),
                event
        );

        return OrderDeleteResponse.from(findOrder);
    }

}
