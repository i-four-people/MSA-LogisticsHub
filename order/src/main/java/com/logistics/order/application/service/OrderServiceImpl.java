package com.logistics.order.application.service;

import com.logistics.order.application.dto.OrderCreateRequest;
import com.logistics.order.application.dto.OrderResponse;
import com.logistics.order.application.dto.PageResponse;
import com.logistics.order.application.dto.event.OrderCreateEvent;
import com.logistics.order.domain.model.Order;
import com.logistics.order.domain.repository.OrderRepository;
import com.logistics.order.domain.service.OrderService;
import com.logistics.order.infrastructure.client.ProductClient;
import com.logistics.order.infrastructure.config.RabbitMQConfig;
import com.logistics.order.presentation.request.SearchParameter;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;
    private final RabbitTemplate rabbitTemplate;

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
                RabbitMQConfig.ORDER_ROUTING_KEY,
                event);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> getOrders(SearchParameter searchParameter) {
        return null;
    }

}
