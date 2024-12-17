package com.logistcshub.user.domain.repository;

import com.logistcshub.user.domain.model.user.UserRoleEnum;
import com.logistcshub.user.presentation.request.deliveryManager.DeliSearchRequest;
import com.logistcshub.user.presentation.response.deliveryManager.DeliSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface DeliveryManagerRepositoryCustom {
    Page<DeliSearchResponse> findAllDeliveryPerson(Pageable pageable, DeliSearchRequest searchRequest, Long userId, UserRoleEnum role);

}
