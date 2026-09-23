package com.logic.project.service;

import com.logic.project.domain.Site;
import com.logic.project.domain.SiteStatusHistory;
import com.logic.project.dto.SiteRequest;
import com.logic.project.dto.SiteResponse;
import com.logic.project.dto.SiteStatusHistoryResponse;
import com.logic.project.dto.SiteUpdateRequest;
import com.logic.project.repository.SiteRepository;
import com.logic.project.repository.SiteStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SiteService {

    private final SiteRepository siteRepository;
    private final SiteStatusHistoryRepository siteStatusHistoryRepository;
    private final com.logic.project.repository.EquipmentRepository equipmentRepository;


    // ========================================
    // 현장 등록
    // ========================================

    @Transactional
    public SiteResponse createSite(SiteRequest request) {

        if (siteRepository.existsBySiteCode(request.siteCode())) {
            throw new IllegalArgumentException(
                    "이미 사용 중인 현장 코드입니다."
            );
        }

        Site site = Site.builder()
                .siteCode(request.siteCode())
                .name(request.name())
                .address(request.address())
                .description(request.description())
                .siteType(request.siteType())
                .status(Site.SiteStatus.NORMAL)
                .managerName(request.managerName())
                .managerTel(request.managerTel())
                .build();

        Site savedSite = siteRepository.save(site);

        return SiteResponse.from(savedSite);
    }


    // ========================================
    // 전체 현장 조회
    // ========================================

    public List<SiteResponse> getSites() {

        return siteRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(SiteResponse::from)
                .toList();
    }


    // ========================================
    // 현장 상세 조회
    // ========================================

    public SiteResponse getSite(Long id) {

        Site site = findSite(id);

        return SiteResponse.from(site);
    }


    // ========================================
    // 현장 코드 조회
    // ========================================

    public SiteResponse getSiteByCode(String siteCode) {

        Site site = siteRepository
                .findBySiteCode(siteCode)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "현장을 찾을 수 없습니다."
                        )
                );

        return SiteResponse.from(site);
    }


    // ========================================
    // 현장명 검색
    // ========================================

    public List<SiteResponse> searchByName(String name) {

        return siteRepository
                .findByNameContainingIgnoreCaseOrderByCreatedAtDesc(name)
                .stream()
                .map(SiteResponse::from)
                .toList();
    }


    // ========================================
    // 주소 검색
    // ========================================

    public List<SiteResponse> searchByAddress(String address) {

        return siteRepository
                .findByAddressContainingIgnoreCaseOrderByCreatedAtDesc(address)
                .stream()
                .map(SiteResponse::from)
                .toList();
    }


    // ========================================
    // 상태별 조회
    // ========================================

    public List<SiteResponse> getSitesByStatus(
            Site.SiteStatus status
    ) {

        return siteRepository
                .findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(SiteResponse::from)
                .toList();
    }


    // ========================================
    // 유형별 조회
    // ========================================

    public List<SiteResponse> getSitesByType(
            Site.SiteType siteType
    ) {

        return siteRepository
                .findBySiteTypeOrderByCreatedAtDesc(siteType)
                .stream()
                .map(SiteResponse::from)
                .toList();
    }


    // ========================================
    // 현장 정보 수정
    // ========================================

    @Transactional
    public SiteResponse updateSite(
            Long id,
            SiteUpdateRequest request
    ) {

        Site site = findSite(id);

        site.setName(request.name());
        site.setAddress(request.address());
        site.setDescription(request.description());
        site.setSiteType(request.siteType());
        site.setManagerName(request.managerName());
        site.setManagerTel(request.managerTel());

        return SiteResponse.from(site);
    }


    // ========================================
    // 현장 상태 변경 + 이력 저장
    // ========================================

    @Transactional
    public SiteResponse updateSiteStatus(
            Long id,
            Site.SiteStatus newStatus,
            String changedBy,
            String reason
    ) {

        Site site = findSite(id);

        // 변경 전 상태 저장
        Site.SiteStatus previousStatus =
                site.getStatus();

        // 같은 상태로 변경하는 경우
        if (previousStatus == newStatus) {
            throw new IllegalArgumentException(
                    "현재 상태와 동일한 상태입니다."
            );
        }

        // 현장 상태 변경
        site.setStatus(newStatus);

        // 상태 변경 이력 생성
        SiteStatusHistory history =
                SiteStatusHistory.builder()
                        .site(site)
                        .previousStatus(previousStatus)
                        .newStatus(newStatus)
                        .changedBy(changedBy)
                        .reason(reason)
                        .build();

        siteStatusHistoryRepository.save(history);

        return SiteResponse.from(site);
    }


    // ========================================
    // 현장 상태 변경 이력 조회
    // ========================================

    public List<SiteStatusHistoryResponse>
    getSiteStatusHistories(Long siteId) {

        // 존재하는 현장인지 먼저 확인
        findSite(siteId);

        return siteStatusHistoryRepository
                .findBySiteIdOrderByChangedAtDesc(siteId)
                .stream()
                .map(SiteStatusHistoryResponse::from)
                .toList();
    }


    // ========================================
    // 현장 삭제
    // ========================================

    @Transactional
    public void deleteSite(Long id) {

        Site site = findSite(id);

        if (equipmentRepository.existsBySiteId(id)) {
            throw new IllegalArgumentException("등록된 장비가 있는 현장은 삭제할 수 없습니다. 장비를 먼저 이동하거나 삭제해주세요.");
        }

        siteRepository.delete(site);
    }


    // ========================================
    // 공통 현장 조회
    // ========================================

    private Site findSite(Long id) {

        return siteRepository
                .findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "현장을 찾을 수 없습니다."
                        )
                );
    }
}
