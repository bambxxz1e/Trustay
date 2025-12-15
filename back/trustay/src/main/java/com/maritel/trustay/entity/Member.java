package com.maritel.trustay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TBL_MEMBER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 100) // 암호화된 비밀번호 길이 고려
    private String passwd;

    @Column(nullable = false, length = 25)
    private String name;

    @OneToOne(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Profile profile;

    @Builder
    public Member(String email, String passwd, String name) {
        this.email = email;
        this.passwd = passwd;
        this.name = name;
    }

    // 이름 변경 편의 메서드
    public void updateName(String name) {
        this.name = name;
    }
}