package com.logic.project.controller;

import com.logic.project.domain.Equipment;
import com.logic.project.dto.EquipmentRequest;
import com.logic.project.dto.EquipmentResponse;
import com.logic.project.dto.EquipmentStatusHistoryResponse;
import com.logic.project.dto.EquipmentUpdateRequest;
import com.logic.project.service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<java.util.Map<String, String>> handleConflict() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(java.util.Map.of("message", "장비 코드가 중복되거나 연결된 데이터가 변경되었습니다. 다시 확인해주세요."));
    }

    @ExceptionHandler({org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
            org.springframework.http.converter.HttpMessageNotReadableException.class,
            org.springframework.web.bind.MissingServletRequestParameterException.class})
    public ResponseEntity<java.util.Map<String, String>> handleInvalidInput() {
        return ResponseEntity.badRequest()
                .body(java.util.Map.of("message", "요청 값이 올바르지 않습니다. 필수 값, 상태, 유형, 날짜 형식을 확인해주세요."));
    }


    // ========================================
    // 장비 등록
    // POST /api/equipments
    // ========================================

    @PostMapping
    public ResponseEntity<EquipmentResponse> createEquipment(
            @Valid @RequestBody EquipmentRequest request
    ) {

        EquipmentResponse response =
                equipmentService.createEquipment(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    // ========================================
    // 전체 장비 조회
    // GET /api/equipments
    // ========================================

    @GetMapping
    public ResponseEntity<List<EquipmentResponse>> getEquipments(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long siteId,
            @RequestParam(required = false) Equipment.EquipmentStatus status,
            @RequestParam(required = false) Equipment.EquipmentType equipmentType) {

        return ResponseEntity.ok(
                equipmentService.getEquipments(name, siteId, status, equipmentType)
        );
    }


    // ========================================
    // 장비 상세 조회
    // GET /api/equipments/1
    // ========================================

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentResponse> getEquipment(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                equipmentService.getEquipment(id)
        );
    }


    // ========================================
    // 장비 코드 조회
    // GET /api/equipments/code/SITE-001
    // ========================================

    @GetMapping("/code/{equipmentCode}")
    public ResponseEntity<EquipmentResponse> getEquipmentByCode(
            @PathVariable String equipmentCode
    ) {

        return ResponseEntity.ok(
                equipmentService.getEquipmentByCode(equipmentCode)
        );
    }


    // ========================================
    // 장비명 검색
    // GET /api/equipments/search/name?name=서울
    // ========================================

    @GetMapping("/search/name")
    public ResponseEntity<List<EquipmentResponse>> searchByName(
            @RequestParam String name
    ) {

        return ResponseEntity.ok(
                equipmentService.getEquipments(name, null, null, null)
        );
    }


    // ========================================
    // 현장별 조회
    // GET /api/equipments/site/1
    // ========================================

    @GetMapping("/site/{siteId}")
    public ResponseEntity<List<EquipmentResponse>> getEquipmentsBySite(@PathVariable Long siteId) {
        return ResponseEntity.ok(equipmentService.getEquipments(null, siteId, null, null));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EquipmentResponse>> getEquipmentsByStatus(
            @PathVariable Equipment.EquipmentStatus status
    ) {

        return ResponseEntity.ok(
                equipmentService.getEquipments(null, null, status, null)
        );
    }


    // ========================================
    // 유형별 조회
    // GET /api/equipments/type/TOLL_GATE
    // ========================================

    @GetMapping("/type/{equipmentType}")
    public ResponseEntity<List<EquipmentResponse>> getEquipmentsByType(
            @PathVariable Equipment.EquipmentType equipmentType
    ) {

        return ResponseEntity.ok(
                equipmentService.getEquipments(null, null, null, equipmentType)
        );
    }


    // ========================================
    // 장비 정보 수정
    // PUT /api/equipments/1
    // ========================================

    @PutMapping("/{id}")
    public ResponseEntity<EquipmentResponse> updateEquipment(
            @PathVariable Long id,
            @Valid @RequestBody EquipmentUpdateRequest request
    ) {

        return ResponseEntity.ok(
                equipmentService.updateEquipment(
                        id,
                        request
                )
        );
    }


    // ========================================
    // 장비 상태 변경 + 이력 저장
    //
    // PATCH
    // /api/equipments/1/status
    // ?status=MAINTENANCE
    // &reason=정기점검
    // ========================================

    @PatchMapping("/{id}/status")
    public ResponseEntity<EquipmentResponse> updateEquipmentStatus(
            @PathVariable Long id,
            @RequestParam Equipment.EquipmentStatus status,
            @RequestParam(required = false) String reason,
            Authentication authentication
    ) {

        // JWT에서 현재 로그인 사용자 가져오기
        String changedBy =
                authentication.getName();

        return ResponseEntity.ok(
                equipmentService.updateEquipmentStatus(
                        id,
                        status,
                        changedBy,
                        reason
                )
        );
    }


    // ========================================
    // 장비 상태 변경 이력 조회
    //
    // GET /api/equipments/1/histories
    // ========================================

    @GetMapping("/{id}/histories")
    public ResponseEntity<List<EquipmentStatusHistoryResponse>>
    getEquipmentStatusHistories(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                equipmentService.getEquipmentStatusHistories(id)
        );
    }


    // ========================================
    // 장비 삭제
    // DELETE /api/equipments/1
    // ========================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(
            @PathVariable Long id
    ) {

        equipmentService.deleteEquipment(id);

        return ResponseEntity
                .noContent()
                .build();
    }
}
