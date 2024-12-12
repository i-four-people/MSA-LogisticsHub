package com.logistics.order.application.service;

import com.logistics.order.application.dto.order.OrderCreateRequest;
import com.logistics.order.application.dto.event.OrderCreateEvent;
import com.logistics.order.domain.model.Order;
import com.logistics.order.domain.repository.OrderRepository;
import com.logistics.order.infrastructure.client.ProductClient;
import com.logistics.order.infrastructure.config.RabbitMQConfig;
import com.logistics.order.presentation.exception.BusinessException;
import com.logistics.order.presentation.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private ProductClient productClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_Success() {
        // Given
        UUID productId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        int quantity = 5;
        int price = 1000;

        OrderCreateRequest request = new OrderCreateRequest(recipientId, productId, quantity, price, null);

        // Mock productClient response
        doNothing().when(productClient).decreaseStock(productId, quantity);

        // Mock orderRepository save response
        Order mockOrder = Order.create(request);
        when(orderRepository.save(any(Order.class)))
                .thenReturn(mockOrder);

        // When
        orderService.createOrder(request);

        // Then
        // Verify that productClient was called
        verify(productClient).decreaseStock(productId, quantity);

        // Verify that orderRepository save was called
        verify(orderRepository).save(any(Order.class));

        // Verify that rabbitTemplate was used to send the event
        ArgumentCaptor<OrderCreateEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreateEvent.class);
        verify(rabbitTemplate).convertAndSend(
                eq(RabbitMQConfig.ORDER_EXCHANGE),
                eq(RabbitMQConfig.ORDER_CREATED_ROUTING_KEY),
                eventCaptor.capture()
        );

        OrderCreateEvent capturedEvent = eventCaptor.getValue();
        assertEquals(mockOrder.getId(), capturedEvent.orderId());

    }

    @Test
    @DisplayName("주문 생성 실패 - 재고 부족")
    void createOrder_ShouldThrowException_WhenStockIsInsufficient() {
        // Given
        UUID productId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        int quantity = 5;
        int price = 1000;

        OrderCreateRequest request = new OrderCreateRequest(recipientId, productId, quantity, price, "Please deliver quickly");

        // Mock productClient response with insufficient stock
        doThrow(new BusinessException(ErrorCode.OUT_OF_STOCK))
                .when(productClient).decreaseStock(productId, quantity);

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> orderService.createOrder(request));

        assertEquals("상품 재고가 부족합니다.", exception.getMessage());

        // Verify that productClient was called
        assertEquals(ErrorCode.OUT_OF_STOCK, exception.getErrorCode());

        // Verify that productClient was called
        verify(productClient).decreaseStock(productId, quantity);

        // Verify that orderRepository save was not called
        verify(orderRepository, never()).save(any(Order.class));

        // Verify that rabbitTemplate was not used
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));
    }

}