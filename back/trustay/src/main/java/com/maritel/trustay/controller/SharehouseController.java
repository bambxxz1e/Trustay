package com.maritel.trustay.controller;

import com.maritel.trustay.constant.ApprovalStatus;
import com.maritel.trustay.dto.req.SharehouseReq;
import com.maritel.trustay.dto.req.SharehouseSearchReq;
import com.maritel.trustay.dto.req.SharehouseUpdateReq;
import com.maritel.trustay.dto.res.*;
import com.maritel.trustay.service.SharehouseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// [중요] 여기 Import가 잘못되어 있었습니다. 아래 것으로 바꿔야 합니다.
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

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
            @Valid @RequestBody SharehouseReq req) {

        String userEmail = principal.getName();
        SharehouseRes response = sharehouseService.registerSharehouse(userEmail, req);

        return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, response));
    }

    @Operation(
            summary = "쉐어하우스 매물 등록(이미지 여러장)",
            description = "multipart/form-data로 매물 정보(req) + 이미지들(images[])를 업로드합니다. 이미지는 선택사항이며 여러 장 업로드 가능합니다.",
            requestBody = @RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = SharehouseReq.class)
                    )
            )
    )
    @PostMapping(value = "/with-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<DataResponse<SharehouseRes>> registerSharehouseWithImages(
            Principal principal,
            @Valid @RequestPart("req") SharehouseReq req,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        String userEmail = principal.getName();
        SharehouseRes response = sharehouseService.registerSharehouse(userEmail, req, images);
        return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, response));
    }

    @Operation(summary = "쉐어하우스 정보 수정", description = "집주인이 본인의 매물 정보를 수정합니다.")
    @PutMapping("/{houseId}")
    public ResponseEntity<DataResponse<Void>> updateSharehouse(
            @PathVariable Long houseId,
            @RequestBody SharehouseUpdateReq req,
            Principal principal) {

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
            @RequestParam ApprovalStatus status,
            Principal principal) { // Principal 추가

        // 로그인한 사용자(관리자 추정)의 이메일 추출
        String email = principal.getName();
        log.info(email);

        // 서비스에 이메일 전달하여 권한 체크 및 승인 수행
        sharehouseService.approveSharehouse(houseId, status, email);

        return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS));
    }

    // --- [수정된 부분: 상세 조회 & 목록 조회] ---

    @Operation(summary = "쉐어하우스 상세 조회", description = "매물 상세 정보를 조회합니다. (조회수 1 증가)")
    @GetMapping("/{houseId}")
    public ResponseEntity<DataResponse<SharehouseResultRes>> getSharehouseDetail(@PathVariable Long houseId) {
        SharehouseResultRes response = sharehouseService.getSharehouseDetail(houseId);
        return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, response));
    }

    @Operation(summary = "쉐어하우스 목록 조회")
    @GetMapping
    public ResponseEntity<DataResponse<PageResponse<SharehouseResultRes>>> getSharehouseList(
            @ModelAttribute SharehouseSearchReq req,
            @PageableDefault(size = 10, sort = "viewCount", direction = Sort.Direction.DESC) Pageable pageable) {

        // 1. 서비스에서 Page 객체를 받아옴
        Page<SharehouseResultRes> resultPage = sharehouseService.getSharehouseList(req, pageable);

        // 2. 우리가 만든 깔끔한 PageResponse로 변환!
        PageResponse<SharehouseResultRes> response = new PageResponse<>(resultPage);

        return ResponseEntity.ok(DataResponse.of(ResponseCode.SUCCESS, response));
    }
}