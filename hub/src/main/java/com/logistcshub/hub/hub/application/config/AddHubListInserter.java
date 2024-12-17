package com.logistcshub.hub.hub.application.config;

import static com.logistcshub.hub.common.exception.application.type.ErrorCode.ALREADY_EXISTS_HUB;

import com.logistcshub.hub.common.exception.RestApiException;
import com.logistcshub.hub.hub.application.service.HubService;
import com.logistcshub.hub.hub.domain.mode.Hub;
import com.logistcshub.hub.hub.domain.repository.HubRepository;
import com.logistcshub.hub.hub.presentation.request.AddHubRequestDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AddHubListInserter {
    private final HubService hubService;

    AddHubRequestDto[] hubs = new AddHubRequestDto[] {
            new AddHubRequestDto("서울특별시 센터", "서울특별시 송파구 송파대로 55"),
            new AddHubRequestDto("경기 북부 센터", "경기도 고양시 덕양구 권율대로 570"),
            new AddHubRequestDto("경기 남부 센터", "경기도 이천시 덕평로 257-21"),
            new AddHubRequestDto("부산광역시 센터", "부산광역시 동구 중앙대로 206"),
            new AddHubRequestDto("대구광역시 센터", "대구광역시 북구 태평로 161"),
            new AddHubRequestDto("인천광역시 센터", "인천광역시 남동구 정각로 29"),
            new AddHubRequestDto("광주광역시 센터", "광주광역시 서구 내방로 111"),
            new AddHubRequestDto("대전광역시 센터", "대전광역시 서구 둔산로 100"),
            new AddHubRequestDto("울산광역시 센터", "울산광역시 남구 중앙로 201"),
            new AddHubRequestDto("세종특별자치시 센터", "세종특별자치시 한누리대로 2130"),
            new AddHubRequestDto("강원특별자치도 센터", "강원특별자치도 춘천시 중앙로 1"),
            new AddHubRequestDto("충청북도 센터", "충청북도 청주시 상당구 상당로 82"),
            new AddHubRequestDto("충청남도 센터", "충청남도 홍성군 홍북읍 충남대로 21"),
            new AddHubRequestDto("전북특별자치도 센터", "전라북도 전주시 완산구 효자로 225"),
            new AddHubRequestDto("전라남도 센터", "전라남도 무안군 삼향읍 오룡길 1"),
            new AddHubRequestDto("경상북도 센터", "경상북도 안동시 풍천면 도청대로 455"),
            new AddHubRequestDto("경상남도 센터", "경상남도 창원시 의창구 중앙대로 300")
    };


    @PostConstruct
    public void init() {
        Long userId = 1L;
        String role = "MASTER";

//        for(AddHubRequestDto hub : hubs) {
//            try {
//                hubService.addHub(userId, role, hub);
//            } catch (RestApiException e) {
//                log.info("exception : {} : {}", hub.name(), e.getErrorCode().getDescription());
//            }
//            try {
//                Thread.sleep(500); // 500 milliseconds
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt(); // 인터럽트 상태 복구
//                System.err.println("Thread was interrupted during sleep.");
//            }
//        }

    }
}
