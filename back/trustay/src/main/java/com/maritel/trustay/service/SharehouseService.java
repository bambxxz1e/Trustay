package com.maritel.trustay.service;

import com.maritel.trustay.constant.ApprovalStatus;
import com.maritel.trustay.constant.Role;
import com.maritel.trustay.dto.req.SharehouseReq;
import com.maritel.trustay.dto.req.SharehouseSearchReq;
import com.maritel.trustay.dto.req.SharehouseUpdateReq;
import com.maritel.trustay.dto.res.SharehouseRes;
import com.maritel.trustay.dto.res.SharehouseResultRes;
import com.maritel.trustay.entity.Member;
import com.maritel.trustay.entity.Profile;
import com.maritel.trustay.entity.Sharehouse;
import com.maritel.trustay.repository.MemberRepository;
import com.maritel.trustay.repository.SharehouseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SharehouseService {

    private final SharehouseRepository sharehouseRepository;
    private final MemberRepository memberRepository;
    private final FileService fileService;

    /**
     * 쉐어하우스 등록
     */
    @Transactional
    public SharehouseRes registerSharehouse(String userEmail, SharehouseReq req) {
        return registerSharehouse(userEmail, req, null);
    }

    /**
     * 쉐어하우스 등록 (이미지 여러장 업로드 지원)
     */
    @Transactional
    public SharehouseRes registerSharehouse(String userEmail, SharehouseReq req, List<MultipartFile> images) {
        // 1. 등록하려는 사용자 조회
        Member member = memberRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        Profile profile = member.getProfile();

        // [추가된 로직] 이 사람이 아직 HOST 권한이 없다면 추가해준다!
        if (!profile.getRoles().contains(Role.HOST)) {
            profile.addRole(Role.HOST);
            // Profile은 영속성 컨텍스트 안에 있으므로, 메서드 종료 시 DB에 자동 반영(Update)됩니다.
        }

        // 2. 옵션 리스트를 String으로 변환 (예: [WIFI, PARKING] -> "WIFI,PARKING")
        String optionsString = null;
        if (req.getOptions() != null && !req.getOptions().isEmpty()) {
            optionsString = String.join(",", req.getOptions());
        }

        String imageUrlsString = null;
        if (images != null && !images.isEmpty()) {
            try {
                List<String> uploadedUrls = fileService.uploadFiles(images);
                if (!uploadedUrls.isEmpty()) {
                    imageUrlsString = String.join(",", uploadedUrls);
                }
            } catch (MalformedURLException | BadRequestException e) {
                throw new IllegalArgumentException("이미지 업로드에 실패했습니다.", e);
            }
        }

        // 3. 엔티티 생성
        Sharehouse sharehouse = Sharehouse.builder()
                .host(member)
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
                .imageUrls(imageUrlsString)
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
    public void approveSharehouse(Long houseId, ApprovalStatus status, String adminEmail) {

        // 1. 요청자(관리자) 조회
        Member admin = memberRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));

        // 2. 권한 확인 (Profile 테이블의 Role 확인)
        // Profile이 없거나, Role이 ADMIN이 아니면 예외 발생
        log.info(admin.getEmail());
        if (admin.getProfile() == null || !admin.getProfile().getRoles().contains(Role.ADMIN)) {
            throw new IllegalStateException("관리자 권한이 없습니다.");
        }

        // 3. 매물 조회 및 상태 변경
        Sharehouse sharehouse = sharehouseRepository.findById(houseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 쉐어하우스가 존재하지 않습니다."));

        sharehouse.changeApprovalStatus(status);
    }

    /**
     * [수정] 쉐어하우스 상세 조회
     * - 조회 시 viewCount 증가
     * - 상세 정보인 SharehouseResultRes 반환
     */
    @Transactional
    public SharehouseResultRes getSharehouseDetail(Long houseId) {
        // 1. 조회수 증가
        sharehouseRepository.updateViewCount(houseId);

        // 2. 조회
        Sharehouse sharehouse = sharehouseRepository.findById(houseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 쉐어하우스가 존재하지 않습니다."));

        return SharehouseResultRes.from(sharehouse);
    }

    /**
     * [수정] 쉐어하우스 목록 조회 (검색 + 페이징 + 정렬)
     */
    public Page<SharehouseResultRes> getSharehouseList(SharehouseSearchReq req, Pageable pageable) {
        // 1. QueryDSL로 만든 커스텀 메서드 호출
        Page<Sharehouse> resultPage = sharehouseRepository.search(req, pageable);

        // 2. DTO로 변환하여 반환
        return resultPage.map(SharehouseResultRes::from);
    }
}