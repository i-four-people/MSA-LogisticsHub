package com.logistcshub.hub.area.domain.model.type;

import static com.logistcshub.hub.common.exception.application.type.ErrorCode.STATE_NOT_FOUND;

import com.logistcshub.hub.common.exception.RestApiException;
import java.util.Arrays;
import java.util.List;

public enum State {
    SEOUL("서울특별시"),
    BUSAN("부산광역시"),
    DAEGU("대구광역시"),
    INCHEON("인천광역시"),
    GWANGJU("광주광역시"),
    DAEJEON("대전광역시"),
    ULSAN("울산광역시"),
    SEJONG("세종특별자치시"),
    GYEONGGI("경기도"),
    GANGWON("강원특별자치도"),
    CHUNGBUK("충청북도"),
    CHUNGNAM("충청남도"),
    JEONBUK("전라북도"),
    JEONNAM("전라남도"),
    GYEONGBUK("경상북도"),
    GYEONGNAM("경상남도"),
    JEJU("제주특별자치도");

    private final String koreanName;

    State(String koreanName) {
        this.koreanName = koreanName;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public static String getKoreanName(State state) {
        return state.getKoreanName();
    }

    public static State findState(String koreanName) {
        return Arrays.stream(State.values())
                .filter(state -> state.getKoreanName().equals(koreanName))
                .findFirst()
                .orElseThrow(() -> new RestApiException(STATE_NOT_FOUND));
    }

    public static List<State> findAllState(String keyword) {
        return Arrays.stream(State.values())
                .filter(state -> state.getKoreanName().contains(keyword))
                .toList();
    }
}
