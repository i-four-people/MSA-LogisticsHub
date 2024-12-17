package com.logistcshub.hub.area.domain.model.type;

import static com.logistcshub.hub.common.exception.application.type.ErrorCode.CITY_NOT_FOUND;

import com.logistcshub.hub.common.exception.RestApiException;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public enum City {
    // 서울특별시
    SONGPA("송파구", State.SEOUL),
    GANGNAM("강남구", State.SEOUL),

    // 경기도
    ICHON("이천시", State.GYEONGGI),
    GOYANG("고양시", State.GYEONGGI),

    // 부산광역시
    DONGGU_BUSAN("동구", State.BUSAN),

    // 대구광역시
    BUKGU_DAEGU("북구", State.DAEGU),

    // 인천광역시
    NAMDONG("남동구", State.INCHEON),

    // 광주광역시
    SEOGU_GWANGJU("서구", State.GWANGJU),

    // 대전광역시
    SEOGU_DAEJEON("서구", State.DAEJEON),

    // 울산광역시
    NAMGU_ULSAN("남구", State.ULSAN),

    // 세종특별자치시
    HANNURO("한누리대로", State.SEJONG),

    // 강원특별자치도
    CHUNCHEON("춘천시", State.GANGWON),

    // 충청북도
    CHUNGJU("청주시", State.CHUNGBUK),

    // 충청남도
    HONGSUNG_CHUNGNAM("홍성군", State.CHUNGNAM),

    // 전라북도
    JEONJU("전주시", State.JEONBUK),

    // 전라남도
    MUAN("무안군", State.JEONNAM),

    // 경상북도
    ANDONG("안동시", State.GYEONGBUK),

    // 경상남도
    CHANGWON("창원시", State.GYEONGNAM);

    private final String koreanName;
    private final State state;

    City(String koreanName, State state) {
        this.koreanName = koreanName;
        this.state = state;
    }

    public String getKoreanName() {
        return koreanName;
    }

    public State getState() {
        return state;
    }

    public static City findCity(String koreanName, State state) {
        return Arrays.stream(City.values())
                .filter(city -> city.getKoreanName().equals(koreanName) && city.getState().equals(state))
                .findFirst()
                .orElseThrow(() -> new RestApiException(CITY_NOT_FOUND));
    }

    public static City findCityIfNoTFoundReturnNull(String koreanName, State state) {
        return Arrays.stream(City.values())
                .filter(city -> city.getKoreanName().equals(koreanName) && city.getState().equals(state))
                .findFirst()
                .orElse(null);
    }

    public static List<City> findAllCitiesByState(State state) {
        return Arrays.stream(City.values())
                .filter(city -> city.state.equals(state))
                .toList();
    }

    public static List<City> findAllCity(String keyword) {
        return Arrays.stream(City.values())
                .filter(city -> city.getKoreanName().contains(keyword))
                .toList();
    }

    public static List<City> findAllCityAndState(String keyword) {
        return Arrays.stream(City.values())
                .filter(city -> city.getKoreanName().contains(keyword) || city.getState().getKoreanName().contains(keyword))
                .toList();
    }
}