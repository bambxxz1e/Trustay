package com.maritel.trustay.dto.req;

import com.maritel.trustay.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.parameters.P;

@Getter
@Setter
@ToString
@Builder
public class SignupReq {

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Pattern(
            regexp = "^[a-zA-Z0-9가-힣]{2,25}$",
            message = "이름은 문자만 입력 가능합니다."
    )
    @Size(min = 2, max = 25, message = "이름은 25자 이하, 2자 이상으로 입력해주세요.")
    private String name;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Pattern(
            regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$",
            message = "이메일 형식이 올바르지 않습니다."
    )
    @Size(max = 100, message = "이메일은 100자 이하로 입력해주세요.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Pattern(
            regexp = "^[a-zA-Z0-9!@#$%^&*?_]{8,50}$",
            message = "비밀번호는 영문자, 숫자, 특수문자(!@#$%^&*?_)만 포함하여 8~50자 사이여야 합니다."
    )
    @Size(min = 8, max = 50, message = "비밀번호는 최소 8자 이상, 최대 50자 이하로 입력해주세요.")
    private String passwd;

    @NotBlank(message = "생년월일은 필수 입력 값입니다.")
    @Pattern(
            regexp = "^\\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01])$",
            message = "생년월일 형식은 yyyy-mm-dd입니다."
    )
    private String brith;

    @NotBlank(message = "전화번호는 필수 입력 값입니다.")
    @Pattern(
            regexp = "^(0\\d{1,2})-(\\d{3,4})-(\\d{4})$",
            message = "전화번호 혈식은 000-0000-0000입니다."
    )
    private String phone;


    public Member toEntity() {
        return Member.builder()
                .name(this.name)
                .email(this.email)
                .passwd(this.passwd)
                .birth(this.brith)
                .phone(this.phone)
                .build();
    }
}
