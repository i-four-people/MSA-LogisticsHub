package com.logistcshub.user.application.config;

import com.logistcshub.user.application.client.HubClient;
import com.logistcshub.user.application.client.dto.HubResponseDto;
import com.logistcshub.user.application.dtos.HubDto;
import com.logistcshub.user.common.response.SuccessResponse;
import com.logistcshub.user.domain.model.deliveryManager.DeliveryManager;
import com.logistcshub.user.domain.model.deliveryManager.HubManager;
import com.logistcshub.user.domain.model.user.User;
import com.logistcshub.user.domain.model.user.UserRoleEnum;
import com.logistcshub.user.infrastructure.repository.DeliveryManagerRepository;
import com.logistcshub.user.infrastructure.repository.HubManagerRepository;
import com.logistcshub.user.infrastructure.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.logistcshub.user.domain.model.deliveryManager.DeliveryManagerType.HUB_PIC;

@Component
@RequiredArgsConstructor
public class UserInserter {
    private final UserRepository userRepository;
    private final HubManagerRepository hubManagerRepository;
    private final HubClient hubClient;
    private final PasswordEncoder passwordEncoder;
    private final DeliveryManagerRepository deliveryManagerRepository;

    @PostConstruct
    public void init() {
        int i = 1;
        String[] slackIds = {"chldudrms94", "U085DQ6BA02", "U08574P7CEA", "U085DNWN1SN"};
        UserRoleEnum[] roles = UserRoleEnum.values();
        int roleIdx = 0;

        User user = userRepository.findById(200L).orElse(null);

        if(user != null) {
            return;
        }

        List<User> saveUsers = new ArrayList<>();
        //saveUsers.add(User.create("user" + i, passwordEncoder.encode( "password") + i, "useremail" + i + "@naver.com", "010-0000-0000", "chldudrms94" ,UserRoleEnum.MASTER ));

        // 유저 등록
        while(i <= 200) {
            saveUsers.add(User.create("user" + i, passwordEncoder.encode( "password" + i), "useremail" + i + "@naver.com", "010-0000-0000", slackIds[i % 4] , roles[roleIdx]));

            if(i == 2 || i == 4 || i == 180) {
                roleIdx++;
            }
            i++;
        }

        saveUsers = userRepository.saveAll(saveUsers);

        List<User> deliveryManagers = saveUsers.stream().filter(saveuser -> saveuser.getRole().equals(UserRoleEnum.DELIVERY_MANAGER)).toList();


        List<HubResponseDto> hubList  = hubClient.getAllHubs("MASTER").getBody().data();

        List<HubManager> saveManagers = new ArrayList<>();
        List<DeliveryManager> saveDeliveryManagers = new ArrayList<>();

        int idx = 0;
        int deliveryManagerIdx = 0;

        for (HubResponseDto hubDto : hubList) {
            if(idx < 10) {
                saveManagers.add(HubManager.create(deliveryManagers.get(deliveryManagerIdx), hubDto.id()));
                deliveryManagerIdx++;
                idx++;
            }
            for(int j = 0; j < 10; j++) {
                saveDeliveryManagers.add(DeliveryManager.from("ksu", deliveryManagers.get(deliveryManagerIdx).getId(), hubDto.id(), HUB_PIC));
                deliveryManagerIdx++;
            }
        }

        deliveryManagerRepository.saveAll(saveDeliveryManagers);

        hubManagerRepository.saveAll(saveManagers);

    }
}