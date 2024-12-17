package com.logistics.delivery.application.scheduler;

import com.logistics.delivery.domain.model.Delivery;
import com.logistics.delivery.domain.model.DeliveryStatus;
import com.logistics.delivery.domain.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryStatusScheduler {

    private final DeliveryService deliveryService;

    @Scheduled(fixedRateString = "${scheduler.delivery-assignment.interval}")
    public void updateDeliveryStatus() {
        log.info("Starting delivery status update task.");

        // 모든 활성화된 배송 가져오기
        List<Delivery> deliveries = deliveryService.findAllByStatusNotIn(
                List.of(DeliveryStatus.DELIVERED, DeliveryStatus.CANCELLED)
        );

        deliveries.forEach(deliveryService::updateStatusForDelivery);
        log.info("Completed delivery status update task.");
    }

}
