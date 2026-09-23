package com.logic.project.service;

import com.logic.project.domain.*;
import com.logic.project.dto.*;
import com.logic.project.repository.*;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EquipmentService {
    private final EquipmentRepository equipmentRepository;
    private final SiteRepository siteRepository;
    private final EquipmentStatusHistoryRepository equipmentStatusHistoryRepository;

    @Transactional
    public EquipmentResponse createEquipment(EquipmentRequest request) {
        String code = request.equipmentCode().trim();
        if (equipmentRepository.existsByEquipmentCode(code)) {
            throw new IllegalArgumentException("이미 사용 중인 장비 코드입니다.");
        }
        Equipment equipment = Equipment.builder()
                .site(findSite(request.siteId()))
                .equipmentCode(code)
                .name(request.name())
                .equipmentType(request.equipmentType())
                .manufacturer(request.manufacturer())
                .modelName(request.modelName())
                .serialNumber(request.serialNumber())
                .installLocation(request.installLocation())
                .installedAt(request.installedAt())
                .ipAddress(request.ipAddress())
                .description(request.description())
                .build();
        return EquipmentResponse.from(equipmentRepository.saveAndFlush(equipment));
    }

    // All supplied filters are combined with AND. DTO conversion occurs inside the transaction.
    public List<EquipmentResponse> getEquipments(String name, Long siteId,
            Equipment.EquipmentStatus status, Equipment.EquipmentType equipmentType) {
        if (siteId != null) findSite(siteId);
        return equipmentRepository.findAll((root, query, cb) -> {
            root.fetch("site");
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.isBlank()) {
                String literal = name.trim().toLowerCase(java.util.Locale.ROOT)
                        .replace("!", "!!").replace("%", "!%").replace("_", "!_");
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + literal + "%", '!'));
            }
            if (siteId != null) predicates.add(cb.equal(root.get("site").get("id"), siteId));
            if (status != null) predicates.add(cb.equal(root.get("status"), status));
            if (equipmentType != null) predicates.add(cb.equal(root.get("equipmentType"), equipmentType));
            return cb.and(predicates.toArray(Predicate[]::new));
        }, Sort.by(Sort.Direction.DESC, "createdAt", "id")).stream().map(EquipmentResponse::from).toList();
    }

    public EquipmentResponse getEquipment(Long id) { return EquipmentResponse.from(findEquipment(id)); }
    public EquipmentResponse getEquipmentByCode(String code) {
        return EquipmentResponse.from(equipmentRepository.findByEquipmentCode(code)
                .orElseThrow(() -> new IllegalArgumentException("장비를 찾을 수 없습니다.")));
    }

    @Transactional
    public EquipmentResponse updateEquipment(Long id, EquipmentUpdateRequest request) {
        Equipment equipment = findForUpdate(id);
        equipment.setSite(findSite(request.siteId()));
        equipment.setName(request.name());
        equipment.setEquipmentType(request.equipmentType());
        equipment.setManufacturer(request.manufacturer());
        equipment.setModelName(request.modelName());
        equipment.setSerialNumber(request.serialNumber());
        equipment.setInstallLocation(request.installLocation());
        equipment.setInstalledAt(request.installedAt());
        equipment.setIpAddress(request.ipAddress());
        equipment.setDescription(request.description());
        equipmentRepository.flush();
        return EquipmentResponse.from(equipment);
    }

    @Transactional
    public EquipmentResponse updateEquipmentStatus(Long id, Equipment.EquipmentStatus status,
            String changedBy, String reason) {
        if (status == null) throw new IllegalArgumentException("상태는 필수입니다.");
        if (reason != null && reason.length() > 500) throw new IllegalArgumentException("변경 사유는 500자 이하로 입력해주세요.");
        Equipment equipment = findForUpdate(id);
        Equipment.EquipmentStatus previous = equipment.getStatus();
        if (previous == status) throw new IllegalArgumentException("현재 상태와 동일한 상태입니다.");
        equipment.setStatus(status);
        equipmentStatusHistoryRepository.save(EquipmentStatusHistory.builder()
                .equipment(equipment).previousStatus(previous).newStatus(status)
                .changedBy(changedBy).reason(reason).build());
        equipmentRepository.flush();
        return EquipmentResponse.from(equipment);
    }

    public List<EquipmentStatusHistoryResponse> getEquipmentStatusHistories(Long id) {
        findEquipment(id);
        return equipmentStatusHistoryRepository.findByEquipmentIdOrderByChangedAtDesc(id)
                .stream().map(EquipmentStatusHistoryResponse::from).toList();
    }

    @Transactional
    public void deleteEquipment(Long id) {
        Equipment equipment = findForUpdate(id);
        equipmentStatusHistoryRepository.deleteByEquipmentId(id);
        equipmentStatusHistoryRepository.flush();
        equipmentRepository.delete(equipment);
    }

    private Equipment findEquipment(Long id) {
        return equipmentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("장비를 찾을 수 없습니다."));
    }
    private Equipment findForUpdate(Long id) {
        return equipmentRepository.findForUpdate(id)
                .orElseThrow(() -> new IllegalArgumentException("장비를 찾을 수 없습니다."));
    }
    private Site findSite(Long id) {
        return siteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("현장을 찾을 수 없습니다."));
    }
}
