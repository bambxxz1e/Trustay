package com.maritel.trustay.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ProfileUpdateReq {
    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Pattern(
            regexp = "^[a-zA-Z0-9가-힣]{2,25}$",
            message = "이름은 문자만 입력 가능합니다."
    )
    @Size(min = 2, max = 25, message = "이름은 25자 이하, 2자 이상으로 입력해주세요.")
    private String name;

    @NotBlank(message = "생년월일은 필수 입력 값입니다.")
    @Pattern(
            regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
            message = "생년월일 형식은 yyyy-mm-dd입니다."
    )
    private String birth;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    @Pattern(
            regexp = "^(0\\d{1,2})-(\\d{3,4})-(\\d{4})$",
            message = "전화번호 혈식은 000-0000-0000입니다."
    )
    private String phone;
}
