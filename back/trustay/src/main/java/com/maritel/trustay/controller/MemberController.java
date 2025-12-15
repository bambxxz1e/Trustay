package com.maritel.trustay.controller;

import com.maritel.trustay.dto.req.LoginReq;
import com.maritel.trustay.dto.req.SignupReq;
import com.maritel.trustay.dto.res.DataResponse;
import com.maritel.trustay.dto.res.ProfileRes;
import com.maritel.trustay.dto.res.ResponseCode;
import com.maritel.trustay.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trustay/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    public ResponseEntity<DataResponse<Void>> signup(@Valid @RequestBody SignupReq requestDto) {
        try {
            memberService.signup(requestDto);
            return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS));
        } catch (IllegalStateException e) {
            return ResponseEntity.ok(DataResponse.of(ResponseCode.ALREADY_EXIST_USER_EMAIL, null));
        }
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public ResponseEntity<DataResponse<Long>> login(@RequestBody LoginReq loginReq) {
        try {
            Long memberId = memberService.login(loginReq);
            // 실제로는 여기서 JWT 토큰 등을 헤더나 바디에 담아줘야 합니다.
            return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, memberId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.ok(DataResponse.of(400, e.getMessage()));
        }
    }
}