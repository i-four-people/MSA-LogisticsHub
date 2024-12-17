package com.logistics.order.application.service;

import com.logistics.order.application.dto.event.OrderCreateEvent;
import com.logistics.order.application.dto.order.OrderCreateRequest;
import com.logistics.order.application.dto.product.ProductResponse;
import com.logistics.order.domain.model.Order;
import com.logistics.order.domain.repository.OrderRepository;
import com.logistics.order.infrastructure.client.ProductClient;
import com.logistics.order.infrastructure.config.RabbitMQProperties;
import com.logistics.order.presentation.exception.BusinessException;
import com.logistics.order.presentation.exception.ErrorCode;
import com.logistics.order.presentation.response.ApiResponse;
import com.logistics.order.presentation.response.MessageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;

import java.lang.reflect.Field;
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

    private RabbitMQProperties rabbitProperties;

    @BeforeEach
    void setUp() {
        rabbitProperties = new RabbitMQProperties();

        RabbitMQProperties.Exchange exchange = new RabbitMQProperties.Exchange();
        exchange.setOrder("order.exchange");
        rabbitProperties.setExchange(exchange);

        RabbitMQProperties.Queues queues = new RabbitMQProperties.Queues();
        queues.setDelivery("order.delivery");
        queues.setProduct("order.product");
        rabbitProperties.setQueues(queues);

        injectRabbitMQProperties(orderService, rabbitProperties);
    }

    private void injectRabbitMQProperties(OrderServiceImpl orderService, RabbitMQProperties rabbitMQProperties) {
        try {
            Field field = OrderServiceImpl.class.getDeclaredField("rabbitProperties");
            field.setAccessible(true);
            field.set(orderService, rabbitMQProperties);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Failed to inject RabbitMQProperties into OrderServiceImpl", e);
        }
    }

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_Success() {
        // Given
        UUID productId = UUID.randomUUID();
        UUID recipientId = UUID.randomUUID();
        UUID companyId = UUID.randomUUID();
        int quantity = 5;
        int price = 1000;

        OrderCreateRequest request = new OrderCreateRequest(recipientId, productId, quantity, price, null, "배송지", "수령자", "슬랙ID");

        // Mock productClient response
        ApiResponse<ProductResponse> result = ApiResponse.success(MessageType.RETRIEVE, new ProductResponse(productId, "Product Name", 10, companyId));
        when(productClient.findProductById(productId)).thenReturn(ResponseEntity.ok(result)); // Mock ProductResponse

        // Mock orderRepository save response
        Order mockOrder = Order.create(request, companyId);
        when(orderRepository.save(any(Order.class)))
                .thenReturn(mockOrder);

        // When
        orderService.createOrder(request);

        // Then
        // Verify that productClient was called
        verify(productClient).findProductById(productId);

        // Verify that orderRepository save was called
        verify(orderRepository).save(any(Order.class));

        // Verify that rabbitTemplate was used to send the event
        ArgumentCaptor<OrderCreateEvent> eventCaptor = ArgumentCaptor.forClass(OrderCreateEvent.class);
        verify(rabbitTemplate).convertAndSend(
                eq(rabbitProperties.getExchange().getOrder()),
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
        UUID companyId = UUID.randomUUID();
        int quantity = 5;
        int price = 1000;

        OrderCreateRequest request = new OrderCreateRequest(recipientId, productId, quantity, price, null, "배송지", "수령자", "슬랙ID");

        // Mock productClient response with insufficient stock
        ApiResponse<ProductResponse> result = ApiResponse.success(MessageType.RETRIEVE, new ProductResponse(productId, "Product Name", 3, companyId));
        when(productClient.findProductById(productId))
                .thenReturn(ResponseEntity.ok(result)); // Mock ProductResponse

        // When & Then
        BusinessException exception = assertThrows(BusinessException.class, () -> orderService.createOrder(request));

        assertEquals("상품 재고가 부족합니다.", exception.getMessage());

        // Verify that productClient was called
        assertEquals(ErrorCode.OUT_OF_STOCK, exception.getErrorCode());

        // Verify that orderRepository save was not called
        verify(orderRepository, never()).save(any(Order.class));

        // Verify that rabbitTemplate was not used
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), Optional.ofNullable(any()));
    }

}