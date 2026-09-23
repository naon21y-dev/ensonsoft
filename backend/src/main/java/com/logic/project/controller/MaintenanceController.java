package com.logic.project.controller;

import com.logic.project.dto.*;
import com.logic.project.service.MaintenanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {
    private final MaintenanceService maintenanceService;

    @PostMapping
    public ResponseEntity<MaintenanceResponse> createMaintenance(
            @Valid @RequestBody MaintenanceRequest request, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(maintenanceService.createMaintenance(request, authentication.getName()));
    }

    @GetMapping
    public ResponseEntity<List<MaintenanceResponse>> getMaintenances() {
        return ResponseEntity.ok(maintenanceService.getMaintenances());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MaintenanceResponse> getMaintenance(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceService.getMaintenance(id));
    }

    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<List<MaintenanceResponse>> getMaintenancesByEquipment(@PathVariable Long equipmentId) {
        return ResponseEntity.ok(maintenanceService.getMaintenancesByEquipment(equipmentId));
    }

    @PatchMapping("/{id}/processing")
    public ResponseEntity<MaintenanceResponse> startProcessing(@PathVariable Long id, Authentication authentication) {
        return ResponseEntity.ok(maintenanceService.startProcessing(id, authentication.getName()));
    }

    @PatchMapping("/{id}/complete")
    public ResponseEntity<MaintenanceResponse> completeMaintenance(@PathVariable Long id,
            @Valid @RequestBody MaintenanceCompleteRequest request, Authentication authentication) {
        return ResponseEntity.ok(maintenanceService.completeMaintenance(id, request, authentication.getName()));
    }

    @GetMapping("/{id}/histories")
    public ResponseEntity<List<MaintenanceProcessHistoryResponse>> getHistories(@PathVariable Long id) {
        return ResponseEntity.ok(maintenanceService.getHistories(id));
    }

    @ExceptionHandler({org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class,
            org.springframework.http.converter.HttpMessageNotReadableException.class})
    public ResponseEntity<Map<String, String>> handleInvalidInput() {
        return ResponseEntity.badRequest().body(Map.of("message", "요청 값 또는 날짜 형식이 올바르지 않습니다."));
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleConflict() {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("message", "연결된 장비 정보를 확인한 뒤 다시 접수해주세요."));
    }
}
