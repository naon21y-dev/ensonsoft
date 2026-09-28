package com.logic.project.repository;

import com.logic.project.domain.Qna;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QnaRepository extends JpaRepository<Qna, Long> {

    // 최신 문의 순
    List<Qna> findAllByOrderByCreatedAtDesc();

    // 내가 작성한 문의
    List<Qna> findByUsernameOrderByCreatedAtDesc(
            String username
    );

    // 상태별 조회
    List<Qna> findByStatusOrderByCreatedAtDesc(
            Qna.Status status
    );

    // 유형별 조회
    List<Qna> findByCategoryOrderByCreatedAtDesc(
            Qna.Category category
    );

    // 제목 검색
    List<Qna> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(
            String keyword
    );
}
