package com.maritel.trustay.dto.req;

import com.maritel.trustay.constant.HouseType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class SharehouseReq {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "상세 설명은 필수입니다.")
    private String description;

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    // 위도, 경도는 프론트에서 지도 API로 추출해서 보내주거나, 백엔드에서 주소를 변환해야 합니다.
    // 여기서는 입력을 받는다고 가정합니다.
    private Double latitude;
    private Double longitude;

    @NotNull(message = "주거 형태를 선택해주세요.")
    private HouseType houseType;

    @NotNull(message = "월세는 필수입니다.")
    private Integer rentPrice;

    @NotNull(message = "보증금은 필수입니다.")
    private Integer deposit;

    private Integer roomCount;
    private Integer bathroomCount;
    private Integer currentResidents;

    // DB에는 String으로 저장되지만, 요청은 편의상 List로 받아서 서비스에서 변환합니다.
    private List<String> options;
}