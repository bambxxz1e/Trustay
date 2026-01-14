package com.maritel.trustay.dto.res;

import com.maritel.trustay.constant.ApprovalStatus;
import com.maritel.trustay.constant.HouseType;
import com.maritel.trustay.entity.Sharehouse;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SharehouseRes {
    private Long id;
    private String title;
    private String address;
    private HouseType houseType;
    private ApprovalStatus approvalStatus;

    public static SharehouseRes from(Sharehouse sharehouse) {
        return SharehouseRes.builder()
                .id(sharehouse.getId())
                .title(sharehouse.getTitle())
                .address(sharehouse.getAddress())
                .houseType(sharehouse.getHouseType())
                .approvalStatus(sharehouse.getApprovalStatus())
                .build();
    }
}