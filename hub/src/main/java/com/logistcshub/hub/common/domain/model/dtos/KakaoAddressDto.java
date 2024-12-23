package com.logistcshub.hub.common.domain.model.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class KakaoAddressDto {
    private String address_name;
    private String b_code;
    private String h_code;
    private String main_address_no;
    private String mountain_yn;
    private String region_1depth_name;
    private String region_2depth_name;
    private String region_3depth_h_name;
    private String region_3depth_name;
    private String sub_address_no;
    private String x;
    private String y;
}
