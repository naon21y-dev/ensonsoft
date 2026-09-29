package com.logic.project.repository;

import com.logic.project.domain.Attendance;
import org.springframework.data.jpa.repository.*;
import java.time.LocalDate;
import java.util.*;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByMemberIdAndWorkDate(Long memberId, LocalDate date);
    Optional<Attendance> findFirstByMemberIdAndCheckOutAtIsNullOrderByCheckInAtDesc(Long memberId);
    List<Attendance> findByMemberIdAndWorkDateBetweenOrderByWorkDateDesc(Long memberId, LocalDate from, LocalDate to);
    @EntityGraph(attributePaths = "member")
    List<Attendance> findByWorkDate(LocalDate date);
}
