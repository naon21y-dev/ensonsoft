package com.logic.project.service;

import com.logic.project.domain.*;
import com.logic.project.dto.*;
import com.logic.project.repository.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import static com.logic.project.domain.Attendance.Status.*;
import static com.logic.project.domain.Attendance.WorkType.*;

@Service
@Transactional(readOnly = true)
public class AttendanceService {
    private final AttendanceRepository attendances;
    private final MemberRepository members;
    private final Clock clock;
    public AttendanceService(AttendanceRepository attendances, MemberRepository members, @Qualifier("attendanceClock") Clock clock) {
        this.attendances = attendances; this.members = members; this.clock = clock;
    }
    private Member employee(String username, boolean lock) {
        Member m = (lock ? members.findForAttendanceUpdate(username) : members.findByUsername(username))
                .orElseThrow(() -> error("employeeNotFound"));
        if (m.getRole() != Member.Role.USER || !m.isEnabled()) throw error("employeeUnavailable");
        return m;
    }
    private IllegalArgumentException error(String code) { return new IllegalArgumentException("attendance.errors." + code); }
    private LocalDate today() { return LocalDate.now(clock); }
    private String notes(String notes) {
        if (notes != null && notes.length() > 2000) throw error("invalidInput");
        return notes == null ? "" : notes.trim();
    }
    public AttendanceResponse.Today today(String username) {
        Member m = employee(username, false);
        Instant now = clock.instant(); LocalDate date = now.atZone(clock.getZone()).toLocalDate();
        Attendance row = attendances.findByMemberIdAndWorkDate(m.getId(), date).orElse(null);
        Attendance open = attendances.findFirstByMemberIdAndCheckOutAtIsNullOrderByCheckInAtDesc(m.getId()).orElse(null);
        return new AttendanceResponse.Today(date, clock.getZone().getId(), now, AttendanceResponse.from(m, date, row, now),
                open == null ? null : AttendanceResponse.from(m, open.getWorkDate(), open, now));
    }
    @Transactional
    public AttendanceResponse checkIn(String username, AttendanceRequest request) {
        Member m = employee(username, true);
        Instant now = clock.instant(); LocalDate date = now.atZone(clock.getZone()).toLocalDate();
        if (attendances.findFirstByMemberIdAndCheckOutAtIsNullOrderByCheckInAtDesc(m.getId()).isPresent()) throw error("alreadyWorking");
        if (attendances.findByMemberIdAndWorkDate(m.getId(), date).isPresent()) throw error("alreadyCheckedIn");
        if (request.workType() == null) throw error("invalidInput");
        Attendance row = attendances.save(Attendance.builder().member(m).workDate(date).checkInAt(now)
                .workType(request.workType()).notes(notes(request.notes())).build());
        return AttendanceResponse.from(m, date, row, now);
    }
    @Transactional
    public AttendanceResponse checkOut(String username) {
        Member m = employee(username, true);
        Attendance row = attendances.findFirstByMemberIdAndCheckOutAtIsNullOrderByCheckInAtDesc(m.getId())
                .orElseThrow(() -> error("notWorking"));
        Instant now = clock.instant();
        if (now.isBefore(row.getCheckInAt())) throw error("clockError");
        row.setCheckOutAt(now);
        return AttendanceResponse.from(m, row.getWorkDate(), row, now);
    }
    @Transactional
    public AttendanceResponse updateNotes(String username, AttendanceRequest.Notes request) {
        Member m = employee(username, true);
        Attendance row = attendances.findFirstByMemberIdAndCheckOutAtIsNullOrderByCheckInAtDesc(m.getId())
                .orElseGet(() -> attendances.findByMemberIdAndWorkDate(m.getId(), today()).orElseThrow(() -> error("noRecord")));
        row.setNotes(notes(request.notes()));
        return AttendanceResponse.from(m, row.getWorkDate(), row, clock.instant());
    }
    public AttendanceResponse.History myHistory(String username, LocalDate from, LocalDate to) {
        return history(employee(username, false), from, to);
    }
    public AttendanceResponse.History employeeHistory(Long id, LocalDate from, LocalDate to) {
        Member m = members.findById(id).orElseThrow(() -> error("employeeNotFound"));
        if (m.getRole() != Member.Role.USER) throw error("employeeNotFound");
        return history(m, from, to);
    }
    private AttendanceResponse.History history(Member m, LocalDate from, LocalDate to) {
        LocalDate end = to == null ? today() : to;
        LocalDate start = from == null ? end.minusDays(30) : from;
        if (start.isAfter(end) || java.time.temporal.ChronoUnit.DAYS.between(start, end) > 365 || end.isAfter(today())) throw error("invalidRange");
        Instant now = clock.instant();
        Map<LocalDate, Attendance> rows = attendances.findByMemberIdAndWorkDateBetweenOrderByWorkDateDesc(m.getId(), start, end)
                .stream().collect(Collectors.toMap(Attendance::getWorkDate, Function.identity()));
        // Include missing dates as not checked in; do not invent absence before registration.
        List<AttendanceResponse> result = new ArrayList<>();
        for (LocalDate date = end; !date.isBefore(start); date = date.minusDays(1)) {
            if (m.getCreatedAt() == null || !date.isBefore(m.getCreatedAt().toLocalDate()))
                result.add(AttendanceResponse.from(m, date, rows.get(date), now));
        }
        return new AttendanceResponse.History(m.getId(), m.getUsername(), m.getName(), start, end, clock.getZone().getId(), now, result);
    }
    public AttendanceResponse.Day day(LocalDate requestedDate, String search, Attendance.Status status, Attendance.WorkType workType) {
        LocalDate date = requestedDate == null ? today() : requestedDate;
        if (date.isAfter(today())) throw error("invalidRange");
        Instant now = clock.instant();
        Map<Long, Attendance> rows = attendances.findByWorkDate(date).stream()
                .collect(Collectors.toMap(a -> a.getMember().getId(), Function.identity()));
        List<AttendanceResponse> all = members.findByRoleOrderByNameAscIdAsc(Member.Role.USER).stream()
                .filter(m -> m.getCreatedAt() == null || !date.isBefore(m.getCreatedAt().toLocalDate()))
                .map(m -> AttendanceResponse.from(m, date, rows.get(m.getId()), now)).toList();
        var summary = new AttendanceResponse.Summary(all.size(), all.stream().filter(a -> a.status() == WORKING).count(),
                all.stream().filter(a -> a.status() == NOT_CHECKED_IN).count(), all.stream().filter(a -> a.status() == CHECKED_OUT).count(),
                all.stream().filter(a -> a.workType() == DUTY).count(), all.stream().filter(a -> a.workType() == EMERGENCY).count(),
                all.stream().filter(a -> a.workType() == SUBSTITUTE).count());
        String query = search == null ? "" : search.trim().toLowerCase(Locale.ROOT);
        var filtered = all.stream().filter(a -> query.isEmpty() || a.username().toLowerCase(Locale.ROOT).contains(query)
                        || a.name().toLowerCase(Locale.ROOT).contains(query))
                .filter(a -> status == null || a.status() == status).filter(a -> workType == null || a.workType() == workType).toList();
        return new AttendanceResponse.Day(date, clock.getZone().getId(), now, summary, filtered);
    }
}
