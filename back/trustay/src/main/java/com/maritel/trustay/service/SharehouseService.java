package com.maritel.trustay.service;

import com.maritel.trustay.constant.ApprovalStatus;
import com.maritel.trustay.dto.req.SharehouseReq;
import com.maritel.trustay.dto.req.SharehouseUpdateReq;
import com.maritel.trustay.dto.res.SharehouseRes;
import com.maritel.trustay.entity.Member;
import com.maritel.trustay.entity.Sharehouse;
import com.maritel.trustay.repository.MemberRepository;
import com.maritel.trustay.repository.SharehouseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SharehouseService {

    private final SharehouseRepository sharehouseRepository;
    private final MemberRepository memberRepository;

    /**
     * 쉐어하우스 등록
     */
    @Transactional
    public SharehouseRes registerSharehouse(String userEmail, SharehouseReq req) {
        // 1. 등록하려는 사용자(Host) 조회
        Member host = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 2. 옵션 리스트를 String으로 변환 (예: [WIFI, PARKING] -> "WIFI,PARKING")
        String optionsString = null;
        if (req.getOptions() != null && !req.getOptions().isEmpty()) {
            optionsString = String.join(",", req.getOptions());
        }

        // 3. 엔티티 생성
        Sharehouse sharehouse = Sharehouse.builder()
                .host(host)
                .title(req.getTitle())
                .description(req.getDescription())
                .address(req.getAddress())
                .latitude(req.getLatitude())
                .longitude(req.getLongitude())
                .houseType(req.getHouseType())
                .rentPrice(req.getRentPrice())
                .deposit(req.getDeposit())
                .roomCount(req.getRoomCount())
                .bathroomCount(req.getBathroomCount())
                .currentResidents(req.getCurrentResidents())
                .options(optionsString)
                .approvalStatus(ApprovalStatus.WAITING) // 기본값: 승인 대기
                .build();

        // 4. 저장
        Sharehouse savedSharehouse = sharehouseRepository.save(sharehouse);

        return SharehouseRes.from(savedSharehouse);
    }


    /**
     * 1. 쉐어하우스 수정
     * - 작성자 본인인지 확인 후 수정
     */
    @Transactional
    public void updateSharehouse(Long houseId, String email, SharehouseUpdateReq req) {
        Sharehouse sharehouse = sharehouseRepository.findById(houseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 쉐어하우스가 존재하지 않습니다."));

        // 작성자 검증 (현재 로그인한 유저와 집주인이 같은지)
        if (!sharehouse.getHost().getEmail().equals(email)) {
            throw new IllegalStateException("수정 권한이 없습니다.");
        }

        // 데이터 업데이트
        sharehouse.updateSharehouse(
                req.getTitle(), req.getDescription(), req.getRentPrice(),
                req.getDeposit(), req.getOptions(), req.getRoomCount(),
                req.getBathroomCount(), req.getCurrentResidents(), req.getHouseType()
        );
    }

    /**
     * 2. 쉐어하우스 삭제
     * - 작성자 본인인지 확인 후 삭제
     */
    @Transactional
    public void deleteSharehouse(Long houseId, String email) {
        Sharehouse sharehouse = sharehouseRepository.findById(houseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 쉐어하우스가 존재하지 않습니다."));

        if (!sharehouse.getHost().getEmail().equals(email)) {
            throw new IllegalStateException("삭제 권한이 없습니다.");
        }

        sharehouseRepository.delete(sharehouse);
    }

    /**
     * 3. 쉐어하우스 승인/거절 (관리자용)
     */
    @Transactional
    public void approveSharehouse(Long houseId, ApprovalStatus status) {
        Sharehouse sharehouse = sharehouseRepository.findById(houseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 쉐어하우스가 존재하지 않습니다."));

        sharehouse.changeApprovalStatus(status);
    }
}