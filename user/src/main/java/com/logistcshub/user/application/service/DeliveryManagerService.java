package com.logistcshub.user.application.service;

import com.github.ksuid.Ksuid;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.logistcshub.user.application.client.HubClient;
import com.logistcshub.user.application.dtos.HubDto;
import com.logistcshub.user.application.mapper.DeliveryManagerMapper;
import com.logistcshub.user.common.exception.UserException;
import com.logistcshub.user.common.message.ExceptionMessage;
import com.logistcshub.user.common.security.UserDetailsImpl;
import com.logistcshub.user.domain.model.deliveryManager.DeliveryManager;
import com.logistcshub.user.domain.model.deliveryManager.DeliveryManagerType;
import com.logistcshub.user.domain.model.deliveryManager.HubManager;
import com.logistcshub.user.domain.model.user.User;
import com.logistcshub.user.domain.model.user.UserRoleEnum;
import com.logistcshub.user.infrastructure.repository.DeliveryManagerRepository;
import com.logistcshub.user.infrastructure.repository.HubManagerRepository;
import com.logistcshub.user.infrastructure.repository.UserRepository;
import com.logistcshub.user.presentation.request.DeliveryManagerUpdateRequest;
import com.logistcshub.user.presentation.request.deliveryManager.DeliSearchRequest;
import com.logistcshub.user.presentation.request.deliveryManager.DeliveryManagerCreate;
import com.logistcshub.user.presentation.request.deliveryManager.DeliveryManagerUpdate;
import com.logistcshub.user.presentation.response.deliveryManager.DeliSearchResponse;
import com.logistcshub.user.presentation.response.deliveryManager.DeliveryManagerDto;
import com.logistcshub.user.presentation.response.deliveryManager.DeliveryManagerResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeliveryManagerService {

    private final DeliveryManagerRepository deliveryManagerRepository;
    private final UserRepository userRepository;
    private final DeliveryManagerMapper deliveryManagerMapper;
    private final HubManagerRepository hubManagerRepository;
    private final HubClient hubClient;

    // 배송 담당자 등록
    @Transactional
    public DeliveryManagerDto create(UserDetailsImpl userDetails,
                                     DeliveryManagerCreate deliveryManagerCreate) {

        Long userId = userDetails.getUserId();
        UserRoleEnum role = userDetails.user().getRole();

        User user = userRepository.findById(deliveryManagerCreate.userId())
                .orElseThrow(() -> new UserException(ExceptionMessage.USER_NOT_FOUND));

        // 등록하려는 유저의 권한이 DELIVERY_MANAGER 인지 확인
        if (!user.getRole().equals(UserRoleEnum.DELIVERY_MANAGER)) {
            throw new UserException(ExceptionMessage.INVALID_DELIVERY_MANAGER);
        }

        // 허브 찾아오기
        HubDto hub = findHub(userId, role, deliveryManagerCreate.hubId());

        // 허브 매니저인 경우
        if (role.equals(UserRoleEnum.HUB_MANAGER)) {

            // 담당 허브만 접근 가능
            checkHub(hub, deliveryManagerCreate.hubId(), userId);
            }

        // 배송담당자 타입이 업체 배송 담당자인 경우 -> 허브 여부 확인
        if (deliveryManagerCreate.deliveryManagerType().equals(DeliveryManagerType.COMPANY_PIC)) {
            if (deliveryManagerCreate.hubId() == null) {
                throw new UserException(ExceptionMessage.HUB_NOT_ASSIGNED);
            }
        }

        String ksuid = Ksuid.newKsuid().toString();

        DeliveryManager createdDeliveryManager =
                deliveryManagerMapper
                        .DeliveryManagerCreateToDeliveryManager(deliveryManagerCreate, ksuid);

        return DeliveryManagerDto.from(deliveryManagerRepository.save(createdDeliveryManager));
    }

    @Transactional(readOnly = true)
    public Page<DeliSearchResponse> getAll(UserDetailsImpl userDetails, Pageable pageable, DeliSearchRequest deliSearchRequest) {
        Long userId = userDetails.getUserId();
        UserRoleEnum role = userDetails.user().getRole();

        return deliveryManagerRepository.findAllDeliveryPerson(pageable, deliSearchRequest, userId, role);
    }

    @Cacheable(cacheNames = "deliveryPersonInfo", key = "#role + ':' + #userId + ':' + #deliveryPersonId")
    public DeliveryManagerDto get(UserDetailsImpl userDetails, Long deliveryManagerId) {
        Long userId = userDetails.getUserId();
        UserRoleEnum role = userDetails.user().getRole();

        DeliveryManager deliveryManager = deliveryManagerRepository.findById(deliveryManagerId)
                .orElseThrow(() -> new UserException(ExceptionMessage.USER_NOT_FOUND));

        // 허브 객체 가져오기
        HubDto hub = findHub(userId, role, deliveryManager.getHubId());

        // 허브 매니저인 경우
        if (role.equals(UserRoleEnum.HUB_MANAGER)) {
            // 담당 허브만 접근 가능
            checkHub(hub, deliveryManager.getHubId(), userId);
        }

        // 배송 담당자인 경우
        if (role.equals(UserRoleEnum.DELIVERY_MANAGER)) {
            // 본인 정보만 조회 가능
            if (!userId.equals(deliveryManager.getUserId())) {
                throw new UserException(ExceptionMessage.ACCESS_DENIED_OWN_INFO);
            }
        }

        return DeliveryManagerDto.from(deliveryManager);
    }

    // 배송 담당자 수정
    @Transactional
    public DeliveryManagerDto update(UserDetailsImpl userDetails, DeliveryManagerUpdate deliveryManagerUpdate,
                                     Long deliveryManagerId) {
        Long userId = userDetails.getUserId();
        UserRoleEnum role = userDetails.user().getRole();

        DeliveryManager deliveryManager = deliveryManagerRepository.findById(deliveryManagerId)
                .orElseThrow(() -> new UserException(ExceptionMessage.USER_NOT_FOUND));

        // 허브 객체 가져오기
        HubDto hub = findHub(userId, role, deliveryManagerUpdate.hubId());

        // 허브 매니저인 경우
        if (role.equals(UserRoleEnum.HUB_MANAGER)) {
            // 담당 허브만 접근 가능
            checkHub(hub, deliveryManagerUpdate.hubId(), userId);
        }

        deliveryManager.update(deliveryManagerUpdate.deliveryManagerType(), deliveryManagerUpdate.hubId());

        return DeliveryManagerDto.from(deliveryManager);
    }

    // 배송 담당자 삭제
    @Transactional
    public String delete(UserDetailsImpl userDetails, Long deliveryManagerId) {
        Long userId = userDetails.getUserId();
        UserRoleEnum role = userDetails.user().getRole();

        DeliveryManager deliveryManager = deliveryManagerRepository.findById(deliveryManagerId)
                .orElseThrow(() -> new UserException(ExceptionMessage.USER_NOT_FOUND));

        HubDto hub = findHub(userId, role, deliveryManager.getHubId());

        // 허브 매니저인 경우
        if (role.equals(UserRoleEnum.HUB_MANAGER)) {
            // 담당 허브만 접근 가능
            checkHub(hub, deliveryManager.getHubId(), userId);
        }

        deliveryManager.delete(userDetails.getUsername());

        return deliveryManagerId + "번 배송 담당자님 삭제 완료 되었습니다.";
    }

     private HubDto findHub(Long userId, UserRoleEnum role, UUID hubId) {

        // HubId 가져오기
        ResponseEntity<?> responseEntity = hubClient.getHub(userId, String.valueOf(role), hubId);

        Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();
        Gson gson = new Gson();

        String contentJson = gson.toJson(
                ((Map<?, ?>) responseBody.get("data"))
        );

        Type listType = new TypeToken<HubDto>() {}.getType();

        return gson.fromJson(contentJson, listType);
    }

    private void checkHub (HubDto hub, UUID requestHubId, Long userId) {
        // 등록하려는 허브매니저 객체
        HubManager hubManager = hubManagerRepository.findByUserId(userId)
                .orElseThrow(() -> new UserException(ExceptionMessage.INVALID_HUB_MANAGER));

        // 허브 존재 여부
        if (hub == null) {
            throw new UserException(ExceptionMessage.HUB_NOT_FOUND);
        }

        // 배송 담당자를 등록하려는 허브가 허브 매니저의 담당 허브인지 확인
        if (!hubManager.getHubId().equals(requestHubId)) {
            throw new UserException(ExceptionMessage.HUB_MANAGER_ACCESS_DENIED);
        }
    }

    // Delivery 호출 Api
    public List<DeliveryManagerResponse> findAvailableManagers(DeliveryManagerType type) {
        List<DeliveryManager> deliveryManagers = deliveryManagerRepository.findByDeliveryManagerTypeAndDeletedAtIsNull(type);

        List<Long> userIds = deliveryManagers.stream()
                .map(DeliveryManager::getUserId)
                .collect(Collectors.toList());

        // 한번에 User 조회
        List<User> users = userRepository.findAllById(userIds);

        // Map으로 User 정보 빠르게 찾기
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, user -> user));

        return deliveryManagers.stream()
                .map(deliveryManager -> {
                    User user = userMap.get(deliveryManager.getUserId());
                    return DeliveryManagerResponse.from(deliveryManager, user);
                })
                .collect(Collectors.toList());
    }

    public void updateHubForManager(Long deliveryManagerId, DeliveryManagerUpdateRequest request) {
        DeliveryManager deliveryManager = deliveryManagerRepository.findById(deliveryManagerId)
                .orElseThrow(() -> new UserException(ExceptionMessage.USER_NOT_FOUND));

        deliveryManager.update(request.hubId());
    }
}
