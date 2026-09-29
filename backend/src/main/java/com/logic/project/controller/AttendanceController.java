package com.logic.project.controller;

import com.logic.project.domain.Attendance;
import com.logic.project.dto.*;
import com.logic.project.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;

@RestController @RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService service;
    @GetMapping("/api/attendance/me/today")
    public AttendanceResponse.Today today(Authentication auth) { return service.today(auth.getName()); }
    @PostMapping("/api/attendance/me/check-in")
    public AttendanceResponse checkIn(Authentication auth, @Valid @RequestBody AttendanceRequest request) { return service.checkIn(auth.getName(), request); }
    @PostMapping("/api/attendance/me/check-out")
    public AttendanceResponse checkOut(Authentication auth) { return service.checkOut(auth.getName()); }
    @PatchMapping("/api/attendance/me/notes")
    public AttendanceResponse notes(Authentication auth, @Valid @RequestBody AttendanceRequest.Notes request) { return service.updateNotes(auth.getName(), request); }
    @GetMapping("/api/attendance/me/history")
    public AttendanceResponse.History history(Authentication auth, @RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to) {
        return service.myHistory(auth.getName(), from, to);
    }
    @GetMapping("/api/admin/attendance")
    public AttendanceResponse.Day day(@RequestParam(required = false) LocalDate date, @RequestParam(required = false) String search,
            @RequestParam(required = false) Attendance.Status status, @RequestParam(required = false) Attendance.WorkType workType) {
        return service.day(date, search, status, workType);
    }
    @GetMapping("/api/admin/attendance/members/{id}")
    public AttendanceResponse.History detail(@PathVariable Long id, @RequestParam(required = false) LocalDate from, @RequestParam(required = false) LocalDate to) {
        return service.employeeHistory(id, from, to);
    }
}
