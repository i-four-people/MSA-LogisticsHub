package com.logistcshub.user.domain.repository;

import com.logistcshub.user.domain.model.deliveryManager.DeliveryManager;
import com.logistcshub.user.domain.model.deliveryManager.DeliveryManagerType;
import com.logistcshub.user.domain.model.user.UserRoleEnum;
import com.logistcshub.user.presentation.request.DeliSearchRequest;
import com.logistcshub.user.presentation.response.DeliSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DeliveryManagerRepositoryCustom {
    Page<DeliSearchResponse> findAllDeliveryPerson(Pageable pageable, DeliSearchRequest searchRequest, Long userId, UserRoleEnum role);

}
