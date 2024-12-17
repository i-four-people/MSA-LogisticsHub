package com.logistcshub.user.application.mapper;

import com.logistcshub.user.domain.model.deliveryManager.DeliveryManager;
import com.logistcshub.user.presentation.request.deliveryManager.DeliveryManagerCreate;
import org.springframework.stereotype.Component;

@Component
public class DeliveryManagerMapper {

    public DeliveryManager DeliveryManagerCreateToDeliveryManager(DeliveryManagerCreate deliveryManagerCreate,
                                                                  String ksuid) {

        return DeliveryManager.from(
                ksuid,
                deliveryManagerCreate.userId(),
                deliveryManagerCreate.hubId(),
                deliveryManagerCreate.deliveryManagerType()
        );
    }
}
