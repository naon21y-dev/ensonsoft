package com.logic.project.repository;

import com.logic.project.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // Lock the employee, including when no attendance row exists yet.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Member m where m.username = :username")
    Optional<Member> findForAttendanceUpdate(@Param("username") String username);

    List<Member> findByRoleOrderByNameAscIdAsc(Member.Role role);

    // 로그인 아이디로 회원 조회
    Optional<Member> findByUsername(String username);

    // 아이디 중복 확인
    boolean existsByUsername(String username);

    // 이메일 중복 확인
    boolean existsByEmail(String email);
}
