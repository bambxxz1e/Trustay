package com.maritel.trustay.controller;

import com.maritel.trustay.constant.ApprovalStatus;
import com.maritel.trustay.dto.req.SharehouseReq;
import com.maritel.trustay.dto.req.SharehouseUpdateReq;
import com.maritel.trustay.dto.res.DataResponse;
import com.maritel.trustay.dto.res.ResponseCode;
import com.maritel.trustay.dto.res.SharehouseRes;
import com.maritel.trustay.service.SharehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/trustay/sharehouses")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Sharehouse API", description = "쉐어하우스 매물 관리")
public class SharehouseController {

    private final SharehouseService sharehouseService;

    @Operation(summary = "쉐어하우스 매물 등록", description = "로그인한 사용자가 호스트로서 매물을 등록합니다.")
    @PostMapping
    public ResponseEntity<DataResponse<SharehouseRes>> registerSharehouse(
            Principal principal,
            @Valid @RequestBody SharehouseReq requestDto) {

        // Principal 객체에서 로그인한 사용자의 이메일 추출 (JwtFilter를 통과했으므로 null이 아님)
        String email = principal.getName();

        SharehouseRes response = sharehouseService.registerSharehouse(email, requestDto);

        return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, response));
    }

    @Operation(summary = "쉐어하우스 정보 수정", description = "집주인이 본인의 매물 정보를 수정합니다.")
    @PutMapping("/{houseId}")
    public ResponseEntity<DataResponse<Void>> updateSharehouse(
            @PathVariable Long houseId,
            @RequestBody SharehouseUpdateReq req,
            Principal principal) {

        // Principal에서 이메일 추출 (로그인한 사용자)
        String email = principal.getName();

        sharehouseService.updateSharehouse(houseId, email, req);
        return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS));
    }

    @Operation(summary = "쉐어하우스 삭제", description = "집주인이 본인의 매물을 삭제합니다.")
    @DeleteMapping("/{houseId}")
    public ResponseEntity<DataResponse<Void>> deleteSharehouse(
            @PathVariable Long houseId,
            Principal principal) {

        String email = principal.getName();

        sharehouseService.deleteSharehouse(houseId, email);
        return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS));
    }

    @Operation(summary = "쉐어하우스 승인 상태 변경", description = "관리자가 매물을 승인(APPROVED)하거나 거절(REJECTED)합니다.")
    @PatchMapping("/{houseId}/approval")
    public ResponseEntity<DataResponse<Void>> approveSharehouse(
            @PathVariable Long houseId,
            @RequestParam ApprovalStatus status) {

        // 실제 운영 환경에서는 여기서 관리자 권한(Role) 체크가 필요합니다.
        sharehouseService.approveSharehouse(houseId, status);
        return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS));
    }
}
