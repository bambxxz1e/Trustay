package com.maritel.trustay.dto.req;

import com.maritel.trustay.constant.PatternConstants;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProfileUpdateReq {

    @Pattern(regexp = PatternConstants.BIRTH_REGEX, message = PatternConstants.BIRTH_MESSAGE)
    private String birth;

    @Pattern(regexp = PatternConstants.PHONE_REGEX, message = PatternConstants.PHONE_MESSAGE)
    private String phone;

    @Size(max = 50, message = "계좌 정보는 50자 이하로 입력해주세요.")
    private String accountInfo;

    // 프로필 이미지는 MultipartFile로 별도 처리
}