package com.logistcshub.hub.hub_transfer.application.config;

import com.logistcshub.hub.hub.application.service.HubService;
import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub.domain.repository.HubRepository;
import com.logistcshub.hub.hub_transfer.application.service.HubTransferService;
import com.logistcshub.hub.hub_transfer.domain.model.HubTransfer;
import com.logistcshub.hub.hub_transfer.domain.repository.HubTransferRepository;
import com.logistcshub.hub.hub_transfer.presentation.request.AddHubTransferRequestDto;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class AddHubTransferInserter {

    private final HubRepository hubRepository;
    private final HubTransferRepository hubTransferRepository;
    private final HubTransferService hubTransferService;

    private static String[][] hubToHub = new String[][] {
            // 경기 남부 센터
            {"경기 남부 센터", "경기 북부 센터"},
            {"경기 남부 센터", "서울특별시 센터"},
            {"경기 남부 센터", "인천광역시 센터"},
            {"경기 남부 센터", "강원특별자치도 센터"},
            {"경기 남부 센터", "경상북도 센터"},
            {"경기 남부 센터", "대전광역시 센터"},
            {"경기 남부 센터", "대구광역시 센터"},

            // 대전 광역시 센터
            {"대전광역시 센터", "충청남도 센터"},
            {"대전광역시 센터", "충청북도 센터"},
            {"대전광역시 센터", "세종특별자치시 센터"},
            {"대전광역시 센터", "전북특별자치도 센터"},
            {"대전광역시 센터", "광주광역시 센터"},
            {"대전광역시 센터", "전라남도 센터"},
            {"대전광역시 센터", "대구광역시 센터"},

            // 대구 광역시 센터
            {"대구광역시 센터", "경상북도 센터"},
            {"대구광역시 센터", "경상남도 센터"},
            {"대구광역시 센터", "부산광역시 센터"},
            {"대구광역시 센터", "울산광역시 센터"},
    };

    @PostConstruct
    public void init() {

        Long userid = 1L;
        String role = "MASTER";
        List<HubTransfer> saveList = new ArrayList<>();

        Map<String, Hub> hubMap = new HashMap<>();

        hubRepository.findAll().forEach(hub ->  hubMap.put(hub.getName(), hub));

        for(String[] hubs : hubToHub) {
            Hub hub1 = hubMap.get(hubs[0]);
            Hub hub2 = hubMap.get(hubs[1]);

            if(!hubTransferRepository.existsByStartHubAndEndHubAndDeletedFalse(hub1, hub2)) {
                HubTransfer hubTransfer = hubTransferService.extracted(hub1, hub2);
                hubTransfer.create(userid);
                saveList.add(hubTransfer);
            }

            if(!hubTransferRepository.existsByStartHubAndEndHubAndDeletedFalse(hub2, hub1)) {
                HubTransfer hubTransfer = hubTransferService.extracted(hub2, hub1);
                hubTransfer.create(userid);
                saveList.add(hubTransfer);
            }

            try {
                Thread.sleep(500); // 500 milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // 인터럽트 상태 복구
                System.err.println("Thread was interrupted during sleep.");
            }
        }

        hubTransferRepository.saveAll(saveList);


    }
}
