package com.logic.project.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "equipments", uniqueConstraints = @UniqueConstraint(name = "uk_equipments_code", columnNames = "equipment_code"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Equipment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @Column(name = "equipment_code", nullable = false, length = 30)
    private String equipmentCode;
    @Column(nullable = false, length = 100)
    private String name;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 30)
    private EquipmentType equipmentType;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 20)
    @Builder.Default
    private EquipmentStatus status = EquipmentStatus.NORMAL;
    @Column(length = 100)
    private String manufacturer;
    @Column(length = 100)
    private String modelName;
    @Column(length = 100)
    private String serialNumber;
    @Column(length = 200)
    private String installLocation;
    private LocalDateTime installedAt;
    @Column(length = 45)
    private String ipAddress;
    @Column(length = 500)
    private String description;
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = LocalDateTime.now();
        if (status == null) status = EquipmentStatus.NORMAL;
    }
    @PreUpdate
    protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum EquipmentType { CCTV, LPR_CAMERA, VEHICLE_DETECTOR, TOLL_EQUIPMENT, VMS, NETWORK, SERVER, ETC }
    public enum EquipmentStatus { NORMAL, WARNING, ERROR, MAINTENANCE, OFFLINE, INACTIVE }
}
