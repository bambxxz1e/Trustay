package com.maritel.trustay.dto.res;
import com.maritel.trustay.entity.Member;
import lombok.*;

@Getter
@Builder
public class ProfileRes {
    private String email;
    private String name;
    private String birth;
    private String phone;

    public static ProfileRes from(Member member) {
        return ProfileRes.builder()
                .email(member.getEmail())
                .name(member.getName())
                // Profile이 없을 경우 null 안전 처리
                .birth(member.getProfile() != null ? member.getProfile().getBirth() : null)
                .phone(member.getProfile() != null ? member.getProfile().getPhone() : null)
                .build();
    }
}