package com.logic.project.controller;

import com.logic.project.domain.Site;
import com.logic.project.dto.SiteRequest;
import com.logic.project.dto.SiteResponse;
import com.logic.project.dto.SiteStatusHistoryResponse;
import com.logic.project.dto.SiteUpdateRequest;
import com.logic.project.service.SiteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sites")
@RequiredArgsConstructor
public class SiteController {

    private final SiteService siteService;


    // ========================================
    // 현장 등록
    // POST /api/sites
    // ========================================

    @PostMapping
    public ResponseEntity<SiteResponse> createSite(
            @Valid @RequestBody SiteRequest request
    ) {

        SiteResponse response =
                siteService.createSite(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // ========================================
    // 전체 현장 조회
    // GET /api/sites
    // ========================================

    @GetMapping
    public ResponseEntity<List<SiteResponse>> getSites() {

        return ResponseEntity.ok(
                siteService.getSites()
        );
    }


    // ========================================
    // 현장 상세 조회
    // GET /api/sites/1
    // ========================================

    @GetMapping("/{id}")
    public ResponseEntity<SiteResponse> getSite(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                siteService.getSite(id)
        );
    }


    // ========================================
    // 현장 코드 조회
    // GET /api/sites/code/SITE-001
    // ========================================

    @GetMapping("/code/{siteCode}")
    public ResponseEntity<SiteResponse> getSiteByCode(
            @PathVariable String siteCode
    ) {

        return ResponseEntity.ok(
                siteService.getSiteByCode(siteCode)
        );
    }


    // ========================================
    // 현장명 검색
    // GET /api/sites/search/name?name=서울
    // ========================================

    @GetMapping("/search/name")
    public ResponseEntity<List<SiteResponse>> searchByName(
            @RequestParam String name
    ) {

        return ResponseEntity.ok(
                siteService.searchByName(name)
        );
    }


    // ========================================
    // 주소 검색
    // GET /api/sites/search/address?address=경기도
    // ========================================

    @GetMapping("/search/address")
    public ResponseEntity<List<SiteResponse>> searchByAddress(
            @RequestParam String address
    ) {

        return ResponseEntity.ok(
                siteService.searchByAddress(address)
        );
    }


    // ========================================
    // 상태별 조회
    // GET /api/sites/status/NORMAL
    // ========================================

    @GetMapping("/status/{status}")
    public ResponseEntity<List<SiteResponse>> getSitesByStatus(
            @PathVariable Site.SiteStatus status
    ) {

        return ResponseEntity.ok(
                siteService.getSitesByStatus(status)
        );
    }


    // ========================================
    // 유형별 조회
    // GET /api/sites/type/TOLL_GATE
    // ========================================

    @GetMapping("/type/{siteType}")
    public ResponseEntity<List<SiteResponse>> getSitesByType(
            @PathVariable Site.SiteType siteType
    ) {

        return ResponseEntity.ok(
                siteService.getSitesByType(siteType)
        );
    }


    // ========================================
    // 현장 정보 수정
    // PUT /api/sites/1
    // ========================================

    @PutMapping("/{id}")
    public ResponseEntity<SiteResponse> updateSite(
            @PathVariable Long id,
            @Valid @RequestBody SiteUpdateRequest request
    ) {

        return ResponseEntity.ok(
                siteService.updateSite(
                        id,
                        request
                )
        );
    }


    // ========================================
    // 현장 상태 변경 + 이력 저장
    //
    // PATCH
    // /api/sites/1/status
    // ?status=MAINTENANCE
    // &reason=정기점검
    // ========================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<SiteResponse> updateSiteStatus(
            @PathVariable Long id,
            @RequestParam Site.SiteStatus status,
            @RequestParam(required = false) String reason,
            Authentication authentication
    ) {

        // JWT에서 현재 로그인 사용자 가져오기
        String changedBy =
                authentication.getName();

        return ResponseEntity.ok(
                siteService.updateSiteStatus(
                        id,
                        status,
                        changedBy,
                        reason
                )
        );
    }


    // ========================================
    // 현장 상태 변경 이력 조회
    //
    // GET /api/sites/1/histories
    // ========================================

    @GetMapping("/{id}/histories")
    public ResponseEntity<List<SiteStatusHistoryResponse>>
    getSiteStatusHistories(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                siteService.getSiteStatusHistories(id)
        );
    }


    // ========================================
    // 현장 삭제
    // DELETE /api/sites/1
    // ========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSite(
            @PathVariable Long id
    ) {

        siteService.deleteSite(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}