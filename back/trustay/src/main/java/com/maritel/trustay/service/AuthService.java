package com.maritel.trustay.service;

import com.maritel.trustay.dto.req.LoginReq;
import com.maritel.trustay.entity.Member;
import com.maritel.trustay.repository.MemberRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class AuthService {
    private final MemberRepository memberRepository;
    //private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final PasswordEncoder passwordEncoder;

    // JWT SECRET KEY (32바이트 이상)
    @Value("${secret_key}")
    private String SECRET_KEY;

    public AuthService(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // 🔹 JWT 서명 키 생성
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String login(LoginReq req) {
        Optional<Member> memberOptional = memberRepository.findByEmail(req.getEmail());
        if (memberOptional.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        Member member = memberOptional.get();
        log.info("member: {}", member.getName());

        //비밀번호 검증
        if (!passwordEncoder.matches(req.getPasswd(), member.getPasswd())) {
            throw new RuntimeException("Invalid Password");
        }


        return Jwts.builder()
                .setSubject(member.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24시간 유효
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

}
