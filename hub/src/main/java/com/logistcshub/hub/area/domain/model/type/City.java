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

    // 경기도
    DEOKYANG("덕양구", State.GYEONGGI),
    ICHON("이천시", State.GYEONGGI),

    // 부산광역시
    JUNG_BU("중구", State.BUSAN),

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
    SANGDANG("상당구", State.CHUNGBUK),

    // 충청남도
    HONGBUK("홍북읍", State.CHUNGNAM),

    // 전북특별자치도
    WANSAN("완산구", State.JEONBUK),

    // 전라남도
    SAMHYANG("삼향읍", State.JEONNAM),

    // 경상북도
    PUNGCHEON("풍천면", State.GYEONGBUK),

    // 경상남도
    UICHANG("의창구", State.GYEONGNAM);

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

    public static List<City> findAllCity(String keyword) {
        return Arrays.stream(City.values())
                .filter(city -> city.getKoreanName().contains(keyword))
                .toList();
    }
}