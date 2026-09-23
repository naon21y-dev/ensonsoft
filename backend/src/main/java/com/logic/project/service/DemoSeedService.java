package com.logic.project.service;

import com.logic.project.domain.*;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DemoSeedService {
    private final EntityManager em;

    // 명시적으로 실행한 경우에만 추가합니다. 기존 행은 수정/삭제하지 않습니다.
    @Transactional
    public void seed() {
        // 여러 프로세스가 동시에 실행해도 같은 시연 데이터를 중복 생성하지 않습니다.
        em.createNativeQuery("select pg_advisory_xact_lock(20260923, 1401)").getSingleResult();
        LocalDateTime now = LocalDateTime.now().withNano(0);
        String[] names = {"서울TG", "판교TG", "수원IC", "동탄IC", "용인JC", "하남JC", "강남 공영주차장", "분당 터널", "한강 교량", "인천 진입도로"};
        String[] addresses = {"경기도 성남시 분당구", "경기도 성남시 판교동", "경기도 수원시 영통구", "경기도 화성시 동탄동", "경기도 용인시 기흥구", "경기도 하남시", "서울특별시 강남구", "경기도 성남시 분당구", "서울특별시 광진구", "인천광역시 남동구"};
        Site.SiteType[] siteTypes = {Site.SiteType.TOLL_GATE, Site.SiteType.TOLL_GATE, Site.SiteType.ROAD, Site.SiteType.ROAD, Site.SiteType.ROAD, Site.SiteType.ROAD, Site.SiteType.PARKING, Site.SiteType.TUNNEL, Site.SiteType.BRIDGE, Site.SiteType.ROAD};
        Site.SiteStatus[] siteStatuses = {Site.SiteStatus.NORMAL, Site.SiteStatus.NORMAL, Site.SiteStatus.WARNING, Site.SiteStatus.NORMAL, Site.SiteStatus.ERROR, Site.SiteStatus.NORMAL, Site.SiteStatus.NORMAL, Site.SiteStatus.MAINTENANCE, Site.SiteStatus.NORMAL, Site.SiteStatus.INACTIVE};
        List<Site> sites = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String code = "DEMO-SITE-%03d".formatted(i + 1);
            var found = em.createQuery("select s from Site s where s.siteCode = :code", Site.class).setParameter("code", code).getResultList();
            Site site;
            if (found.isEmpty()) {
                site = Site.builder().siteCode(code).name(names[i] + " 시연 현장").address(addresses[i])
                        .siteType(siteTypes[i]).status(siteStatuses[i]).managerName("시연 운영팀")
                        .description("개발/시연용 샘플 현장").build();
                em.persist(site);
                if (site.getStatus() != Site.SiteStatus.NORMAL) {
                    em.persist(SiteStatusHistory.builder().site(site).previousStatus(Site.SiteStatus.NORMAL)
                            .newStatus(site.getStatus()).changedBy("demo-seed").reason("시연용 현장 상태 구성")
                            .changedAt(now.minusHours(6)).build());
                }
            } else site = found.getFirst();
            sites.add(site);
        }
        Equipment.EquipmentType[] types = {Equipment.EquipmentType.CCTV, Equipment.EquipmentType.LPR_CAMERA, Equipment.EquipmentType.VEHICLE_DETECTOR, Equipment.EquipmentType.NETWORK};
        String[] equipmentNames = {"관제 CCTV", "차량번호 인식 카메라", "차량 검지기", "통신 스위치"};
        // 40대 중 정상 24, 주의 4, 장애 4, 점검중 4, 오프라인 2, 운영중지 2.
        List<Equipment> equipments = new ArrayList<>();
        for (int i = 0; i < 40; i++) {
            String code = "DEMO-EQ-%03d".formatted(i + 1);
            var found = em.createQuery("select e from Equipment e where e.equipmentCode = :code", Equipment.class).setParameter("code", code).getResultList();
            Equipment equipment;
            if (found.isEmpty()) {
                Equipment.EquipmentStatus status = i < 24 ? Equipment.EquipmentStatus.NORMAL : i < 28 ? Equipment.EquipmentStatus.WARNING
                        : i < 32 ? Equipment.EquipmentStatus.ERROR : i < 36 ? Equipment.EquipmentStatus.MAINTENANCE
                        : i < 38 ? Equipment.EquipmentStatus.OFFLINE : Equipment.EquipmentStatus.INACTIVE;
                equipment = Equipment.builder().equipmentCode(code).site(sites.get(i / 4))
                        .name(names[i / 4] + " " + equipmentNames[i % 4]).equipmentType(types[i % 4]).status(status)
                        .manufacturer("시연 제조사").modelName("DEMO-" + types[i % 4]).serialNumber("DEMO-SN-" + (i + 1))
                        .installLocation((i % 4 + 1) + "차로 관제함").installedAt(now.minusMonths(6))
                        .ipAddress("192.0.2." + (i + 1)).description("개발/시연용 샘플 장비").build();
                em.persist(equipment);
                if (status != Equipment.EquipmentStatus.NORMAL) {
                    em.persist(EquipmentStatusHistory.builder().equipment(equipment).previousStatus(Equipment.EquipmentStatus.NORMAL)
                            .newStatus(status).changedBy("demo-seed").reason("시연용 장비 상태 구성").changedAt(now.minusHours(3)).build());
                }
            } else equipment = found.getFirst();
            equipments.add(equipment);
        }
        String[] titles = {"영상 수신 끊김", "차량번호 인식률 저하", "차량 검지 신호 지연", "통신 연결 불안정", "장비 전원 점검", "영상 화질 저하"};
        for (int i = 0; i < 18; i++) {
            String code = "DEMO-MNT-%03d".formatted(i + 1);
            if (em.createQuery("select count(m) from Maintenance m where m.maintenanceCode = :code", Long.class).setParameter("code", code).getSingleResult() > 0) continue;
            var status = Maintenance.MaintenanceStatus.values()[i % 3];
            LocalDateTime occurred = now.minusMinutes(40L + i * 95L);
            Maintenance maintenance = Maintenance.builder().maintenanceCode(code).equipment(equipments.get((i * 2 + 24) % 40))
                    .title(titles[i % titles.length]).content("[시연 데이터] 현장 점검 중 " + titles[i % titles.length] + " 증상이 확인되었습니다.")
                    .status(status).occurredAt(occurred).reportedAt(occurred.plusMinutes(2)).reportedBy("demo-reporter")
                    .result(status == Maintenance.MaintenanceStatus.COMPLETED ? "연결부 점검 및 설정 보정 후 정상 동작 확인" : null)
                    .completedAt(status == Maintenance.MaintenanceStatus.COMPLETED ? occurred.plusMinutes(30) : null).build();
            em.persist(maintenance);
            maintenanceHistory(maintenance, null, Maintenance.MaintenanceStatus.REPORTED, "demo-reporter", "장애 접수", occurred.plusMinutes(2));
            if (status != Maintenance.MaintenanceStatus.REPORTED) {
                maintenanceHistory(maintenance, Maintenance.MaintenanceStatus.REPORTED, Maintenance.MaintenanceStatus.IN_PROGRESS,
                        "demo-engineer", "현장 담당자 확인 및 점검 시작", occurred.plusMinutes(8));
            }
            if (status == Maintenance.MaintenanceStatus.COMPLETED) {
                maintenanceHistory(maintenance, Maintenance.MaintenanceStatus.IN_PROGRESS, status,
                        "demo-engineer", maintenance.getResult(), maintenance.getCompletedAt());
            }
        }
        for (int i = 0; i < 24; i++) {
            // 신규 컬럼 없이 시연 전용 cameraId를 안정적인 seed 식별자로 사용합니다.
            String cameraId = "DEMO-EVENT-%03d".formatted(i + 1);
            if (em.createQuery("select count(e) from VideoEvent e where e.cameraId = :camera", Long.class).setParameter("camera", cameraId).getSingleResult() > 0) continue;
            var status = VideoEvent.EventStatus.values()[i % 3];
            LocalDateTime occurred = now.minusMinutes(15L + i * 17L);
            VideoEvent event = VideoEvent.builder().cameraId(cameraId).location(names[i % 10] + " " + (i % 4 + 1) + "차로")
                    .eventType(VideoEvent.EventType.values()[i % 6]).status(status).severity(VideoEvent.Severity.values()[(i / 3) % 3])
                    .occurredAt(occurred).description("[시연 데이터] 영상분석 장비에서 감지한 관제 이벤트")
                    .processor(status == VideoEvent.EventStatus.UNPROCESSED ? null : "demo-operator")
                    .processedAt(status == VideoEvent.EventStatus.COMPLETED ? occurred.plusMinutes(10) : null).build();
            em.persist(event);
            if (status != VideoEvent.EventStatus.UNPROCESSED) {
                em.persist(EventProcessHistory.builder().videoEvent(event).status(VideoEvent.EventStatus.PROCESSING)
                        .processor("demo-operator").description("현장 영상 확인 시작").processedAt(occurred.plusMinutes(3)).build());
            }
            if (status == VideoEvent.EventStatus.COMPLETED) {
                em.persist(EventProcessHistory.builder().videoEvent(event).status(status).processor("demo-operator")
                        .description("영상 확인 및 현장 조치 완료").processedAt(event.getProcessedAt()).build());
            }
        }
        em.flush();
    }

    private void maintenanceHistory(Maintenance maintenance, Maintenance.MaintenanceStatus previous,
            Maintenance.MaintenanceStatus status, String actor, String description, LocalDateTime time) {
        em.persist(MaintenanceProcessHistory.builder().maintenance(maintenance).previousStatus(previous)
                .newStatus(status).processedBy(actor).description(description).processedAt(time).build());
    }
}
