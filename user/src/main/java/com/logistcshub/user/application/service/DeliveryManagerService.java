package com.logistcshub.user.application.service;

import com.github.ksuid.Ksuid;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.logistcshub.user.application.DeliveryManagerMapper;
import com.logistcshub.user.application.client.HubClient;
import com.logistcshub.user.application.dtos.DeliveryManagerDto;
import com.logistcshub.user.application.dtos.HubDto;
import com.logistcshub.user.application.dtos.HubResponse;
import com.logistcshub.user.application.security.UserDetailsImpl;
import com.logistcshub.user.domain.model.*;
import com.logistcshub.user.infrastructure.repository.DeliveryManagerRepository;
import com.logistcshub.user.infrastructure.repository.HubManagerRepository;
import com.logistcshub.user.infrastructure.repository.UserRepository;
import com.logistcshub.user.presentation.request.DeliveryManagerCreate;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import org.springframework.security.access.AccessDeniedException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryManagerService {

    private final DeliveryManagerRepository deliveryManagerRepository;
    private final UserRepository userRepository;
    private final DeliveryManagerMapper deliveryManagerMapper;
    private final HubManagerRepository hubManagerRepository;
    private final HubClient hubClient;

    @Transactional
    public DeliveryManagerDto create(UserDetailsImpl userDetails,
                                     DeliveryManagerCreate deliveryManagerCreate) {

        Long userId = userDetails.getUserId();
        String role = String.valueOf(userDetails.user().getRole());

        User user = userRepository.findById(deliveryManagerCreate.userId())
                .orElseThrow(() -> new EntityNotFoundException("등록하지 않은 유저입니다."));

        // 등록하려는 허브매니저 객체
        HubManager hubManager = hubManagerRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("등록되지 않은 허브 매니저입니다."));

        // 허브 찾아오기
        HubDto hub = findHub(userId, role, deliveryManagerCreate.hubId());

        // 허브 매니저인 경우
        if (role.equals(UserRoleEnum.HUB_MANAGER)) {

            // 담당 허브만 접근 가능
            checkHub(hub, hubManager.getHubId(), deliveryManagerCreate.hubId());
            }

        // 배송담당자 타입이 업체 배송 담당자인 경우 -> 소속 허브 확인
        if (deliveryManagerCreate.deliveryManagerType().equals(DeliveryManagerType.COMPANY_PIC)) {
            if (hub == null) {
                throw new EntityNotFoundException("존재하지 않는 허브입니다.");
            }
        }

        String ksuid = Ksuid.newKsuid().toString();

        DeliveryManager createdDeliveryManager =
                deliveryManagerMapper
                        .DeliveryManagerCreateToDeliveryManager(deliveryManagerCreate, ksuid);

        return DeliveryManagerDto.from(deliveryManagerRepository.save(createdDeliveryManager));
    }

     private HubDto findHub(Long userId, String role, UUID hubId) {

        // HubId 가져오기
        ResponseEntity<?> responseEntity = hubClient.getHub(userId, role, hubId);

        Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();
        Gson gson = new Gson();

        String contentJson = gson.toJson(
                ((Map<?, ?>) responseBody.get("data"))
        );

        Type listType = new TypeToken<HubDto>() {}.getType();

        return gson.fromJson(contentJson, listType);
    }

    private void checkHub (HubDto hub, UUID managerHubId, UUID requestHubId) {
        // 허브 존재 여부
        if (hub == null) {
            throw new EntityNotFoundException("존재하지 않는 허브입니다.");
        }

        // 배송 담당자를 등록하려는 허브가 허브 매니저의 담당 허브인지 확인
        if (managerHubId.equals(requestHubId)) {
            throw new AccessDeniedException("매니저님의 담당 허브가 아닙니다.");
        }
    }
}
