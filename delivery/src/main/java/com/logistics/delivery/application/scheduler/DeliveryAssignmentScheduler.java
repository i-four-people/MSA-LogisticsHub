package com.logistics.delivery.application.scheduler;

import com.logistics.delivery.domain.service.DeliveryManagerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryAssignmentScheduler {

    private final DeliveryManagerService deliveryManagerService;

    /**
     * 배정되지 않은 경로를 확인하고 배송 담당자를 배정
     */
    @Scheduled(fixedRateString = "${scheduler.delivery-assignment.interval}")
    public void assignManagersToPendingRoutes() {
        try {
            log.info("Starting scheduled task to assign delivery managers.");
            deliveryManagerService.assignManagersForPendingRoutes();
            log.info("Completed scheduled task for assigning delivery managers.");
        } catch (Exception e) {
            log.error("Error occurred during delivery manager assignment: {}", e.getMessage(), e);
        }
    }

}
