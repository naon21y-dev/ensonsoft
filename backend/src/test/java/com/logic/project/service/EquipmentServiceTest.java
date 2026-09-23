package com.logic.project.service;

import com.logic.project.domain.*;
import com.logic.project.dto.*;
import com.logic.project.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {
    @Mock EquipmentRepository equipmentRepository;
    @Mock SiteRepository siteRepository;
    @Mock EquipmentStatusHistoryRepository historyRepository;
    EquipmentService service;
    Site site;
    Equipment equipment;

    @BeforeEach
    void setup() {
        service = new EquipmentService(equipmentRepository, siteRepository, historyRepository);
        site = Site.builder().id(1L).siteCode("SITE-001").name("서울TG").build();
        equipment = Equipment.builder().id(2L).site(site).equipmentCode("EQ-001")
                .name("CCTV").equipmentType(Equipment.EquipmentType.CCTV).build();
    }

    EquipmentRequest request() {
        return new EquipmentRequest(1L, " EQ-001 ", "CCTV", Equipment.EquipmentType.CCTV,
                null, null, null, null, null, null, null);
    }

    @Test
    void createUsesExistingSiteAndDefaultsToNormal() {
        when(siteRepository.findById(1L)).thenReturn(Optional.of(site));
        when(equipmentRepository.saveAndFlush(any())).thenAnswer(invocation -> invocation.getArgument(0));
        EquipmentResponse response = service.createEquipment(request());
        assertEquals("EQ-001", response.equipmentCode());
        assertEquals(1L, response.siteId());
        assertEquals(Equipment.EquipmentStatus.NORMAL, response.status());
    }

    @Test
    void duplicateCodeIsRejectedBeforeSaving() {
        when(equipmentRepository.existsByEquipmentCode("EQ-001")).thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> service.createEquipment(request()));
        verify(equipmentRepository, never()).saveAndFlush(any());
    }

    @Test
    void nonexistentSiteCannotReceiveEquipment() {
        when(siteRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.createEquipment(request()));
        verify(equipmentRepository, never()).saveAndFlush(any());
    }

    @Test
    void statusChangeRecordsPreviousStatusActorAndReason() {
        when(equipmentRepository.findForUpdate(2L)).thenReturn(Optional.of(equipment));
        EquipmentResponse response = service.updateEquipmentStatus(2L, Equipment.EquipmentStatus.ERROR, "user", "통신장애");
        ArgumentCaptor<EquipmentStatusHistory> captor = ArgumentCaptor.forClass(EquipmentStatusHistory.class);
        verify(historyRepository).save(captor.capture());
        assertEquals(Equipment.EquipmentStatus.NORMAL, captor.getValue().getPreviousStatus());
        assertEquals(Equipment.EquipmentStatus.ERROR, captor.getValue().getNewStatus());
        assertEquals("user", captor.getValue().getChangedBy());
        assertEquals("통신장애", captor.getValue().getReason());
        assertSame(equipment, captor.getValue().getEquipment());
        assertEquals(Equipment.EquipmentStatus.ERROR, response.status());
    }

    @Test
    void sameStatusDoesNotCreateHistory() {
        when(equipmentRepository.findForUpdate(2L)).thenReturn(Optional.of(equipment));
        assertThrows(IllegalArgumentException.class,
                () -> service.updateEquipmentStatus(2L, Equipment.EquipmentStatus.NORMAL, "user", null));
        verifyNoInteractions(historyRepository);
    }

    @Test
    void invalidReasonCannotChangeStatus() {
        assertThrows(IllegalArgumentException.class,
                () -> service.updateEquipmentStatus(2L, Equipment.EquipmentStatus.ERROR, "user", "a".repeat(501)));
        verifyNoInteractions(historyRepository, equipmentRepository);
    }

    @Test
    void updatePreservesCodeAndStatusAndCanMoveToAnotherSite() {
        Site other = Site.builder().id(3L).siteCode("SITE-002").name("다른 현장").build();
        when(equipmentRepository.findForUpdate(2L)).thenReturn(Optional.of(equipment));
        when(siteRepository.findById(3L)).thenReturn(Optional.of(other));
        EquipmentResponse response = service.updateEquipment(2L, new EquipmentUpdateRequest(3L,
                "변경 장비", Equipment.EquipmentType.NETWORK, null, null, null, null, null, null, null));
        assertEquals(3L, response.siteId());
        assertEquals("EQ-001", response.equipmentCode());
        assertEquals(Equipment.EquipmentStatus.NORMAL, response.status());
        assertEquals("변경 장비", response.name());
        verifyNoInteractions(historyRepository);
    }

    @Test
    void deleteRemovesOnlyThisEquipmentsHistoriesBeforeEquipment() {
        when(equipmentRepository.findForUpdate(2L)).thenReturn(Optional.of(equipment));
        service.deleteEquipment(2L);
        var order = inOrder(historyRepository, equipmentRepository);
        order.verify(historyRepository).deleteByEquipmentId(2L);
        order.verify(historyRepository).flush();
        order.verify(equipmentRepository).delete(equipment);
        verifyNoInteractions(siteRepository);
    }
}
