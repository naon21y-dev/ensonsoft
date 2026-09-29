package com.logic.project.dto;

import com.logic.project.domain.Attendance.WorkType;
import jakarta.validation.constraints.*;

public record AttendanceRequest(@NotNull WorkType workType, @Size(max = 2000) String notes) {
    public record Notes(@Size(max = 2000) String notes) {}
}
