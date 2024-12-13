//package com.logistcshub.user.application.service;
//
//import com.github.ksuid.Ksuid;
//import com.logistcshub.user.application.DeliveryManagerMapper;
//import com.logistcshub.user.application.client.HubClient;
//import com.logistcshub.user.application.dtos.DeliveryManagerDto;
//import com.logistcshub.user.application.dtos.HubResponse;
//import com.logistcshub.user.application.security.UserDetailsImpl;
//import com.logistcshub.user.domain.model.DeliveryManager;
//import com.logistcshub.user.domain.model.User;
//import com.logistcshub.user.domain.model.UserRoleEnum;
//import com.logistcshub.user.domain.repository.DeliveryManagerRepository;
//import com.logistcshub.user.domain.repository.UserRepository;
//import com.logistcshub.user.presentation.request.DeliveryManagerCreate;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//@Service
//@RequiredArgsConstructor
//public class DeliveryManagerService {
//
//    private final DeliveryManagerRepository deliveryManagerRepository;
//    private final UserRepository userRepository;
//    private final DeliveryManagerMapper deliveryManagerMapper;
//    private final HubClient hubClient;
//
//    @Transactional
//    public DeliveryManagerDto create(UserDetailsImpl userDetails,
//                                     DeliveryManagerCreate deliveryManagerCreate) {
//
//        Long userId = userDetails.getUserId();
//        UserRoleEnum role = userDetails.user().getRole();
//
//        User user = userRepository.findById(deliveryManagerCreate.userId())
//                .orElseThrow(() -> new EntityNotFoundException("등록하지 않은 유저입니다."));
//
//        // 소속된 허브 존재 여부
//        if (role.equals(UserRoleEnum.HUB_MANAGER)) {
//            HubResponse hub = hubClient.getHub(deliveryManagerCreate.hubId());
//
//            if (!hub.id().equals(deliveryManagerCreate.hubId())) {
//
//            }
//        }
//
//            String ksuid = Ksuid.newKsuid().toString();
//
//            DeliveryManager createdDeliveryManager =
//                    deliveryManagerMapper
//                            .DeliveryManagerCreateToDeliveryManager(deliveryManagerCreate, ksuid);
//
//            return DeliveryManagerDto.from(deliveryManagerRepository.save(createdDeliveryManager));
//
//    }
//}
