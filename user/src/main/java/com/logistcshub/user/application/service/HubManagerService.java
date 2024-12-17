package com.logistcshub.user.application.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.logistcshub.user.application.client.HubClient;
import com.logistcshub.user.application.dtos.HubDto;
import com.logistcshub.user.common.security.UserDetailsImpl;
import com.logistcshub.user.domain.model.deliveryManager.HubManager;
import com.logistcshub.user.domain.model.user.User;
import com.logistcshub.user.domain.model.user.UserRoleEnum;
import com.logistcshub.user.infrastructure.repository.HubManagerRepository;
import com.logistcshub.user.infrastructure.repository.UserRepository;
import com.logistcshub.user.presentation.request.deliveryManager.HubManagerRequest;
import com.logistcshub.user.presentation.response.deliveryManager.HubManagerResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class HubManagerService {

    private final UserRepository userRepository;
    private final HubManagerRepository hubManagerRepository;
    private final HubClient hubClient;

    public HubManagerResponse createHubManager(UserDetailsImpl userDetails, HubManagerRequest hubManagerRequest) throws AccessDeniedException {

        User user = userRepository.findById(hubManagerRequest.userId())
                .orElseThrow(() -> new EntityNotFoundException("등록하지 않은 유저입니다."));

        if (!user.getRole().equals(UserRoleEnum.HUB_MANAGER)) {
            throw new AccessDeniedException("해당 유저의 권한이 허브 매니저가 아닙니다.");
        }

        Long userId = userDetails.getUserId();
        String role = String.valueOf(userDetails.user().getRole());

        // HubId 가져오기
        ResponseEntity<?> responseEntity = hubClient.getHub(userId, role, hubManagerRequest.hubId());

        Map<String, Object> responseBody = (Map<String, Object>) responseEntity.getBody();
        Gson gson = new Gson();

        String contentJson = gson.toJson(
                ((Map<?, ?>) responseBody.get("data"))
        );

        Type listType = new TypeToken<HubDto>() {}.getType();
        HubDto hub = gson.fromJson(contentJson, listType);

        HubManager hubManager = HubManager.create(user, hub.id());

        hubManagerRepository.save(hubManager);

        return HubManagerResponse.of(hubManager);
    }
}
