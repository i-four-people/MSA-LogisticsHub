package com.logistics.delivery.infrastructure.listener;

import com.logistics.delivery.application.dto.event.OrderCreateEvent;
import com.logistics.delivery.domain.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderEventListener {

    private final DeliveryRepository deliveryRepository;

    @RabbitListener(queues = "order.created.queue")
    @Transactional
    public void handleOrderCreated(OrderCreateEvent event) {
        // 배송 생성

        // 배송 정보 저장
    }
}
