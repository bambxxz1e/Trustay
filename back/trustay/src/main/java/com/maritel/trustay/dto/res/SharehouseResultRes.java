package com.maritel.trustay.dto.res;

import com.maritel.trustay.entity.Sharehouse;
import com.maritel.trustay.constant.HouseType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SharehouseResultRes {
    private Long id;
    private String title;
    private String description;
    private String address;
    private HouseType houseType;
    private Integer rentPrice;
    private Integer deposit;
    private Integer roomCount;
    private Integer bathroomCount;
    private String options;
    private Integer viewCount;
    private String hostName; // 집주인 이름 표시

    // Entity -> DTO 변환 메서드
    public static SharehouseResultRes from(Sharehouse sharehouse) {
        return SharehouseResultRes.builder()
                .id(sharehouse.getId())
                .title(sharehouse.getTitle())
                .description(sharehouse.getDescription())
                .address(sharehouse.getAddress())
                .houseType(sharehouse.getHouseType())
                .rentPrice(sharehouse.getRentPrice())
                .deposit(sharehouse.getDeposit())
                .roomCount(sharehouse.getRoomCount())
                .bathroomCount(sharehouse.getBathroomCount())
                .options(sharehouse.getOptions())
                .viewCount(sharehouse.getViewCount())
                .hostName(sharehouse.getHost().getName())
                .build();
    }
}