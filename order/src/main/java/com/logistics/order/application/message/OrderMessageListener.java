package com.logistics.order.application.message;

import com.logistics.order.application.dto.event.EventType;
import com.logistics.order.application.dto.event.consume.DeliveryCreateConsume;
import com.logistics.order.application.util.EventUtil;
import com.logistics.order.domain.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMessageListener {

    private final OrderService orderService;

    @RabbitListener(queues = "${message.receive.order}")
    public void handleOrderCreatedEvent(String message) {
        EventType eventType = EventUtil.extractEventType(message);
        switch (eventType) {
            case DELIVERY_CREATED -> {
                DeliveryCreateConsume deliveryCreateConsume = EventUtil.deserializeEvent(message, DeliveryCreateConsume.class);
                orderService.updateOrderWithDeliveryId(deliveryCreateConsume);
            }
        }

    }

}
