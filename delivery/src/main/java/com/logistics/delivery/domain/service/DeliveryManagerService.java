package com.logistics.delivery.domain.service;

public interface DeliveryManagerService {

    // 배송 경로에 배송 담당자 배정
    void assignManagersForPendingRoutes();

}
