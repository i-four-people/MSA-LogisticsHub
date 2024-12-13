package com.logistcshub.user.application;

import com.logistcshub.user.domain.model.DeliveryManager;
import com.logistcshub.user.presentation.request.DeliveryManagerCreate;
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
