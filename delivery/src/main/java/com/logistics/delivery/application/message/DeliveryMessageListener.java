package com.logistics.delivery.application.message;

import com.logistics.delivery.application.dto.event.EventType;
import com.logistics.delivery.application.dto.event.consume.OrderCreateConsume;
import com.logistics.delivery.application.dto.event.consume.OrderDeleteConsume;
import com.logistics.delivery.application.util.EventUtil;
import com.logistics.delivery.domain.service.DeliveryService;
import com.logistics.delivery.presentation.auth.AuthContext;
import com.logistics.delivery.presentation.auth.AuthHeaderInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryMessageListener {

    private final DeliveryService deliveryService;

    @RabbitListener(queues = "${message.receive.delivery}")
    public void handleOrderCreatedEvent(String message) {
        EventType eventType = EventUtil.extractEventType(message);
        log.info("handleOrderCreatedEvent eventType: {}", eventType);

        switch (eventType) {
            case ORDER_CREATED -> {
                // 배송 생성 로직 호출
                OrderCreateConsume orderCreateConsume = EventUtil.deserializeEvent(message, OrderCreateConsume.class);
                AuthContext.set(new AuthHeaderInfo(orderCreateConsume.userId(), orderCreateConsume.role()));
                deliveryService.createDelivery(orderCreateConsume);
            }
            case ORDER_DELETED -> {
                OrderDeleteConsume orderDeleteConsume = EventUtil.deserializeEvent(message, OrderDeleteConsume.class);
                AuthContext.set(new AuthHeaderInfo(orderDeleteConsume.userId(), orderDeleteConsume.role()));
                deliveryService.deleteDeliveryById(orderDeleteConsume.deliveryId());
            }
        }



    }

}
