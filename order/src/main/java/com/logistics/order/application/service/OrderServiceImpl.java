package com.logistics.order.application.service;

import com.logistics.order.application.dto.*;
import com.logistics.order.application.dto.company.CompanyResponse;
import com.logistics.order.application.dto.event.OrderCreateEvent;
import com.logistics.order.application.dto.order.*;
import com.logistics.order.application.dto.product.ProductResponse;
import com.logistics.order.domain.model.Order;
import com.logistics.order.domain.repository.OrderRepository;
import com.logistics.order.domain.service.OrderService;
import com.logistics.order.infrastructure.client.CompanyClient;
import com.logistics.order.infrastructure.client.ProductClient;
import com.logistics.order.infrastructure.config.RabbitMQConfig;
import com.logistics.order.application.dto.SearchParameter;
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

    private final ProductClient productClient;
    private final CompanyClient companyClient;

    @Override
    public void createOrder(OrderCreateRequest request) {

        // 상품 수량 확인 및 감소
        productClient.decreaseStock(request.productId(), request.quantity());

        // 주문 정보 저장
        Order savedOrder = orderRepository.save(Order.create(request));

        // 이벤트 생성
        OrderCreateEvent event = OrderCreateEvent.of(savedOrder);

        // 이벤트 발행
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_CREATED_ROUTING_KEY,
                event);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getOrders(SearchParameter searchParameter) {
        Page<Order> orders = findOrders(searchParameter);

        // 업체 정보 및 상품 정보 조회
        List<UUID> recipientCompanyIds = orders.map(Order::getRecipientCompanyId).stream().distinct().toList();
        List<CompanyResponse> recipientCompanies = companyClient.findCompaniesByIds(recipientCompanyIds);

        List<UUID> requestCompanyIds = orders.map(Order::getRequesterCompanyId).stream().distinct().toList();
        List<CompanyResponse> requestCompanies = companyClient.findCompaniesByIds(requestCompanyIds);

        List<UUID> productIds = orders.map(Order::getProductId).stream().distinct().toList();
        List<ProductResponse> products = productClient.findProductsByIds(productIds);

        // 응답값 반환
        Map<UUID, CompanyResponse> recipientCompanyMap = recipientCompanies.stream().collect(Collectors.toMap(CompanyResponse::companyId, c -> c));
        Map<UUID, CompanyResponse> requestCompanyMap = requestCompanies.stream().collect(Collectors.toMap(CompanyResponse::companyId, c -> c));
        Map<UUID, ProductResponse> productMap = products.stream().collect(Collectors.toMap(ProductResponse::productId, p -> p));

        Page<OrderResponse> results = orders.map(order -> {
            CompanyResponse recipientCompany = recipientCompanyMap.get(order.getRecipientCompanyId());
            CompanyResponse requestCompany = requestCompanyMap.get(order.getRequesterCompanyId());
            ProductResponse product = productMap.get(order.getProductId());
            return OrderResponse.from(order, recipientCompany, requestCompany, product);
        });

        return PageResponse.of(results);
    }

    private Page<Order> findOrders(SearchParameter searchParameter) {
        if ("RECIPIENT_NAME".equals(searchParameter.searchType()) || "REQUESTER_NAME".equals(searchParameter.searchType())) {
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
        CompanyResponse requestCompany = companyClient.findCompanyById(findOrder.getRequesterCompanyId());
        ProductResponse product = productClient.findProductById(findOrder.getProductId());

        return OrderDetailResponse.from(findOrder, recipientCompany, requestCompany, product);
    }

    @Override
    public OrderDetailResponse updateOrderById(UUID orderId, OrderUpdateRequest request) {

        Order findOrder = orderRepository.findById(orderId).orElseThrow(
                () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)
        );

        // 상품 수량 확인 및 감소
        productClient.decreaseStock(findOrder.getProductId(), request.quantity());

        findOrder.update(request);

        // 업체 정보 및 상품 정보 조회
        CompanyResponse recipientCompany = companyClient.findCompanyById(findOrder.getRecipientCompanyId());
        CompanyResponse requestCompany = companyClient.findCompanyById(findOrder.getRequesterCompanyId());
        ProductResponse product = productClient.findProductById(findOrder.getProductId());

        return OrderDetailResponse.from(findOrder, recipientCompany, requestCompany, product);
    }

    @Override
    public OrderDeleteResponse deleteOrderById(UUID orderId) {

        Order findOrder = orderRepository.findById(orderId).orElseThrow(
                () -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND)
        );

        if (findOrder.isDelete()) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        // 이벤트 생성
        OrderCreateEvent event = OrderCreateEvent.of(findOrder);

        // 이벤트 발행
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ORDER_EXCHANGE,
                RabbitMQConfig.ORDER_DELETED_ROUTING_KEY,
                event);

        return OrderDeleteResponse.from(findOrder);
    }

}
