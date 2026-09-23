package com.logic.project.repository;

import com.logic.project.domain.Site;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SiteRepository
        extends JpaRepository<Site, Long> {


    // ========================================
    // 전체 현장 조회
    // 최신 등록순
    // ========================================

    List<Site> findAllByOrderByCreatedAtDesc();


    // ========================================
    // 현장 코드로 조회
    // 예: SITE-001
    // ========================================

    Optional<Site> findBySiteCode(
            String siteCode
    );


    // ========================================
    // 현장 코드 중복 확인
    // ========================================

    boolean existsBySiteCode(
            String siteCode
    );


    // ========================================
    // 현장명 검색
    // ========================================

    List<Site>
    findByNameContainingIgnoreCaseOrderByCreatedAtDesc(
            String name
    );


    // ========================================
    // 주소 검색
    // ========================================

    List<Site>
    findByAddressContainingIgnoreCaseOrderByCreatedAtDesc(
            String address
    );


    // ========================================
    // 현장 상태별 조회
    // NORMAL / WARNING / ERROR ...
    // ========================================

    List<Site>
    findByStatusOrderByCreatedAtDesc(
            Site.SiteStatus status
    );


    // ========================================
    // 현장 유형별 조회
    // TOLL_GATE / TUNNEL / ROAD ...
    // ========================================

    List<Site>
    findBySiteTypeOrderByCreatedAtDesc(
            Site.SiteType siteType
    );
}