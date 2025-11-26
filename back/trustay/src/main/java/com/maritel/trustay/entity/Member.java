package com.maritel.trustay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Table(name = "TBL_MEMBER")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "email", updatable = false, length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "name", length = 25, nullable = false)
    private String name;

    @Column(name = "passwd", length = 50, nullable = false)
    private String passwd;

    @Column(name = "birth", updatable = false, length = 25, nullable = false)
    private String birth;

    @Column(name = "phone",  length = 25, nullable = false)
    private String phone;

}