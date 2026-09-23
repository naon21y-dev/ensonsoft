package com.logic.project.service;

import com.logic.project.domain.*;
import com.logic.project.dto.*;
import com.logic.project.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import static com.logic.project.domain.Maintenance.MaintenanceStatus.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MaintenanceService {
    private final MaintenanceRepository maintenanceRepository;
    private final MaintenanceProcessHistoryRepository historyRepository;
    private final EquipmentRepository equipmentRepository;

    @Transactional
    public MaintenanceResponse createMaintenance(MaintenanceRequest request, String reportedBy) {
        Equipment equipment = equipmentRepository.findById(request.equipmentId())
                .orElseThrow(() -> new IllegalArgumentException("장비를 찾을 수 없습니다."));
        Maintenance maintenance = Maintenance.builder()
                .maintenanceCode("MNT-" + UUID.randomUUID())
                .equipment(equipment).title(request.title().trim()).content(request.content().trim())
                .occurredAt(request.occurredAt()).reportedBy(reportedBy).build();
        maintenanceRepository.save(maintenance);
        saveHistory(maintenance, null, reportedBy, "장애 접수");
        return MaintenanceResponse.from(maintenance);
    }

    public List<MaintenanceResponse> getMaintenances() {
        return maintenanceRepository.findAllByOrderByReportedAtDescIdDesc()
                .stream().map(MaintenanceResponse::from).toList();
    }

    public MaintenanceResponse getMaintenance(Long id) {
        return MaintenanceResponse.from(findMaintenance(id));
    }

    public List<MaintenanceResponse> getMaintenancesByEquipment(Long equipmentId) {
        if (!equipmentRepository.existsById(equipmentId)) throw new IllegalArgumentException("장비를 찾을 수 없습니다.");
        return maintenanceRepository.findByEquipmentIdOrderByReportedAtDescIdDesc(equipmentId)
                .stream().map(MaintenanceResponse::from).toList();
    }

    @Transactional
    public MaintenanceResponse startProcessing(Long id, String processedBy) {
        Maintenance maintenance = findForUpdate(id);
        if (maintenance.getStatus() != REPORTED) throw new IllegalArgumentException("접수 상태의 장애만 처리를 시작할 수 있습니다.");
        maintenance.setStatus(IN_PROGRESS);
        saveHistory(maintenance, REPORTED, processedBy, "장애 처리 시작");
        return MaintenanceResponse.from(maintenance);
    }

    @Transactional
    public MaintenanceResponse completeMaintenance(Long id, MaintenanceCompleteRequest request, String processedBy) {
        if (request.result() == null || request.result().isBlank() || request.result().length() > 2000) {
            throw new IllegalArgumentException("처리 결과를 1~2000자로 입력해주세요.");
        }
        Maintenance maintenance = findForUpdate(id);
        if (maintenance.getStatus() != IN_PROGRESS) throw new IllegalArgumentException("처리중인 장애만 완료할 수 있습니다.");
        maintenance.setStatus(COMPLETED);
        maintenance.setResult(request.result().trim());
        maintenance.setCompletedAt(LocalDateTime.now());
        saveHistory(maintenance, IN_PROGRESS, processedBy, maintenance.getResult());
        return MaintenanceResponse.from(maintenance);
    }

    public List<MaintenanceProcessHistoryResponse> getHistories(Long id) {
        findMaintenance(id);
        return historyRepository.findByMaintenanceIdOrderByProcessedAtDescIdDesc(id)
                .stream().map(MaintenanceProcessHistoryResponse::from).toList();
    }

    private void saveHistory(Maintenance maintenance, Maintenance.MaintenanceStatus previous,
            String processedBy, String description) {
        historyRepository.save(MaintenanceProcessHistory.builder().maintenance(maintenance)
                .previousStatus(previous).newStatus(maintenance.getStatus())
                .processedBy(processedBy).description(description).build());
    }

    private Maintenance findMaintenance(Long id) {
        return maintenanceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("장애를 찾을 수 없습니다."));
    }

    private Maintenance findForUpdate(Long id) {
        return maintenanceRepository.findForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("장애를 찾을 수 없습니다."));
    }
}
