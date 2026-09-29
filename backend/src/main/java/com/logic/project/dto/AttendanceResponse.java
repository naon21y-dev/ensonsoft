package com.logic.project.dto;

import com.logic.project.domain.Attendance;
import com.logic.project.domain.Member;
import java.time.*;
import java.util.List;

public record AttendanceResponse(Long id, Long memberId, String username, String name,
        LocalDate workDate, Instant checkInAt, Instant checkOutAt, long workedSeconds,
        Attendance.WorkType workType, Attendance.Status status, String notes) {
    public static AttendanceResponse from(Member member, LocalDate date, Attendance row, Instant now) {
        return new AttendanceResponse(row == null ? null : row.getId(), member.getId(), member.getUsername(), member.getName(),
                date, row == null ? null : row.getCheckInAt(), row == null ? null : row.getCheckOutAt(),
                row == null ? 0 : Math.max(0, Duration.between(row.getCheckInAt(), row.getCheckOutAt() == null ? now : row.getCheckOutAt()).getSeconds()),
                row == null ? null : row.getWorkType(), row == null ? Attendance.Status.NOT_CHECKED_IN
                : row.getCheckOutAt() == null ? Attendance.Status.WORKING : Attendance.Status.CHECKED_OUT,
                row == null || row.getNotes() == null ? "" : row.getNotes());
    }
    public record Today(LocalDate date, String zone, Instant serverTime, AttendanceResponse attendance, AttendanceResponse activeAttendance) {}
    public record Summary(long total, long working, long notCheckedIn, long checkedOut, long duty, long emergency, long substitute) {}
    public record Day(LocalDate date, String zone, Instant serverTime, Summary summary, List<AttendanceResponse> employees) {}
    public record History(Long memberId, String username, String name, LocalDate from, LocalDate to,
                          String zone, Instant serverTime, List<AttendanceResponse> records) {}
}
