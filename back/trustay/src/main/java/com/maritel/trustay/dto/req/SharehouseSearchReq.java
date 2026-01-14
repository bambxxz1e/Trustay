package com.maritel.trustay.dto.req;

import com.maritel.trustay.constant.HouseType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class SharehouseSearchReq {
    private String keyword;       // 검색어 (제목, 주소)
    private String address;       // 지역 필터
    private HouseType houseType;  // 주거 형태 (APARTMENT, HOUSE 등)
    private Integer minPrice;     // 최소 가격
    private Integer maxPrice;     // 최대 가격
}