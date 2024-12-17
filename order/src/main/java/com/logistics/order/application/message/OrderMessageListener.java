package com.logistics.order.application.message;

import com.logistics.order.application.dto.event.EventType;
import com.logistics.order.application.dto.event.consume.DeliveryCreateConsume;
import com.logistics.order.application.util.EventUtil;
import com.logistics.order.domain.service.OrderService;
import com.logistics.order.presentation.auth.AuthContext;
import com.logistics.order.presentation.auth.AuthHeaderInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderMessageListener {

    private final OrderService orderService;

    @RabbitListener(queues = "${message.receive.order}")
    public void handleOrderCreatedEvent(String message) {
        EventType eventType = EventUtil.extractEventType(message);
        log.info("handleOrderCreatedEvent eventType: {}", eventType);
        switch (eventType) {
            case DELIVERY_CREATED -> {
                DeliveryCreateConsume deliveryCreateConsume = EventUtil.deserializeEvent(message, DeliveryCreateConsume.class);
                AuthContext.set(new AuthHeaderInfo(deliveryCreateConsume.userId(), deliveryCreateConsume.role()));
                orderService.updateOrderWithDeliveryId(deliveryCreateConsume);
            }
        }

    }

}
