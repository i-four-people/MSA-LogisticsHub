package com.logistics.delivery.application.message;

import com.logistics.delivery.application.dto.event.EventType;
import com.logistics.delivery.application.dto.event.consume.OrderCreateConsume;
import com.logistics.delivery.application.util.EventUtil;
import com.logistics.delivery.domain.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DeliveryMessageListener {

    private final DeliveryService deliveryService;

    @RabbitListener(queues = "${message.receive.delivery}")
    public void handleOrderCreatedEvent(String message) {
        EventType eventType = EventUtil.extractEventType(message);
        switch (eventType) {
            case ORDER_CREATED -> {
                // 배송 생성 로직 호출
                OrderCreateConsume orderCreateConsume = EventUtil.deserializeEvent(message, OrderCreateConsume.class);
                deliveryService.createDelivery(orderCreateConsume);
            }
            case ORDER_DELETED -> {

            }
        }

    }

}
