package com.maritel.trustay.service;

import com.maritel.trustay.dto.req.LoginReq;
import com.maritel.trustay.dto.req.SignupReq;
import com.maritel.trustay.dto.req.ProfileUpdateReq;
import com.maritel.trustay.dto.res.ProfileRes;
import com.maritel.trustay.entity.Member;
import com.maritel.trustay.entity.Profile;
import com.maritel.trustay.repository.MemberRepository;
import com.maritel.trustay.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 1. 회원가입
     */
    @Transactional
    public void signup(SignupReq dto) {
        // 이메일 중복 검사
        if (memberRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalStateException("이미 존재하는 이메일입니다.");
        }

        // 1. Member 엔티티 생성 (이름, 이메일, 비번)
        Member member = Member.builder()
                .email(dto.getEmail())
                .passwd(passwordEncoder.encode(dto.getPasswd()))
                .name(dto.getName())
                .build();

        // 2. Member 저장 (이때 id가 생성됨)
        memberRepository.save(member);

        // 3. Profile 엔티티 생성 (생년월일, 전화번호, Member 연결)
        Profile profile = Profile.builder()
                .member(member)        // FK 설정
                .birth(dto.getBirth()) // DTO의 birth 사용
                .phone(dto.getPhone()) // DTO의 phone 사용
                .build();

        // 4. Profile 저장
        profileRepository.save(profile);
    }

    /**
     * 2. 로그인
     */
    public Long login(LoginReq dto) {
        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(dto.getPasswd(), member.getPasswd())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return member.getId();
    }

    /**
     * 3. 프로필 조회 (변경 없음, 다만 ProfileRes 내부 구현 확인 필요)
     */
    public ProfileRes getProfile(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // ProfileRes.from(member) 안에서 member.getProfile().getPhone() 등을 호출해야 함
        return ProfileRes.from(member);
    }

    /**
     * 4. 프로필 수정
     */
    @Transactional
    public void updateProfile(Long memberId, ProfileUpdateReq dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));

        // 1. Member 정보 수정 (이름)
        if (dto.getName() != null) {
            member.updateName(dto.getName());
        }

        // 2. Profile 정보 수정 (전화번호, 생년월일)
        Profile profile = member.getProfile();

        if (profile != null) {
            // 이미 프로필이 있으면 업데이트
            profile.updateProfile(dto.getPhone(), dto.getBirth());
        } else {
            // 방어 코드: 혹시 프로필이 없다면 새로 생성
            Profile newProfile = Profile.builder()
                    .member(member)
                    .phone(dto.getPhone())
                    .birth(dto.getBirth())
                    .build();
            profileRepository.save(newProfile);
        }
    }
}