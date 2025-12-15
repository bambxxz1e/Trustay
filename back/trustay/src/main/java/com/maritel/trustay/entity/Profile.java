package com.maritel.trustay.entity;

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

    @Builder
    public Profile(Member member, String birth, String phone) {
        this.member = member;
        this.birth = birth;
        this.phone = phone;
    }

    public void updateProfile(String phone, String birth) {
        this.phone = phone;
        this.birth = birth;
    }
}