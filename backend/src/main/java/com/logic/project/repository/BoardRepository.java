package com.logic.project.repository;

import com.logic.project.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 최신 게시글 순으로 조회
    List<Board> findAllByOrderByCreatedAtDesc();

    // 제목 검색
    List<Board> findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(String keyword);

    // 작성자 검색
    List<Board> findByWriterContainingIgnoreCaseOrderByCreatedAtDesc(String writer);
}