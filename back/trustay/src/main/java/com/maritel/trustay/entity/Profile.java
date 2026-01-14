package com.maritel.trustay.entity;

import com.maritel.trustay.constant.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_PROFILE")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Profile {

    @Id
    private Long memberId;

    @MapsId // Member의 ID를 PK이자 FK로 사용
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 25)
    private String birth;

    @Column(length = 25)
    private String phone;

    // --- Member에서 이동된 필드 ---
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Role role; // HOST(집주인), TENANT(세입자), ADMIN(관리자)

    @Column(length = 50)
    private String accountInfo; // 정산용 계좌 정보

    // --- 추가된 필드 ---
    @Column(length = 500)
    private String profileImageUrl; // 프로필 사진 URL

    @Builder
    public Profile(Member member, String birth, String phone, Role role, String accountInfo, String profileImageUrl) {
        this.member = member;
        this.birth = birth;
        this.phone = phone;
        this.role = role;
        this.accountInfo = accountInfo;
        this.profileImageUrl = profileImageUrl;
    }

    public void updateProfile(String phone, String birth) {
        this.phone = phone;
        this.birth = birth;
    }

    public void updateAccountInfo(String accountInfo) {
        this.accountInfo = accountInfo;
    }

    public void updateProfileImage(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public void updateRole(Role role) {
        this.role = role;
    }
}