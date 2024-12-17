package com.logistics.delivery.application.scheduler;

import com.logistics.delivery.domain.model.Delivery;
import com.logistics.delivery.domain.service.DeliveryManagerService;
import com.logistics.delivery.domain.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryAssignmentScheduler {

    private final DeliveryService deliveryService;
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

    /**
     * 업체 배송 담당자가 배정되지 않은 배송을 처리
     */
    @Scheduled(fixedRateString = "${scheduler.delivery-assignment.interval}")
    public void assignManagersForUnassignedDeliveries() {
        try {
            log.info("Starting scheduled task to assign company delivery managers for unassigned deliveries.");

            // 담당자가 배정되지 않은 배송 목록 조회
            List<Delivery> unassignedDeliveries = deliveryService.findUnassignedDeliveries();

            if (unassignedDeliveries.isEmpty()) {
                log.info("No unassigned deliveries found for processing.");
                return;
            }

            log.info("Found {} unassigned deliveries. Attempting to assign managers...", unassignedDeliveries.size());

            // 각 배송에 대해 담당자 배정 시도
            unassignedDeliveries.forEach(delivery -> {
                try {
                    deliveryService.assignCompanyDeliveryManager(delivery); // 담당자 배정
                    log.info("Successfully assigned manager for delivery ID: {}", delivery.getId());
                } catch (Exception e) {
                    log.error("Failed to assign manager for delivery ID {}: {}", delivery.getId(), e.getMessage(), e);
                }
            });

            log.info("Completed scheduled task for assigning company delivery managers.");
        } catch (Exception e) {
            log.error("Error occurred during delivery manager assignment: {}", e.getMessage(), e);
        }
    }

}
