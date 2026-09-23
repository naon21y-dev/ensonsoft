package com.logic.project.service;

import com.logic.project.domain.Board;
import com.logic.project.dto.BoardRequest;
import com.logic.project.dto.BoardResponse;
import com.logic.project.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;

    // 게시글 전체 조회 + 검색
    public List<BoardResponse> getBoards(String keyword, String writer) {

        List<Board> boards;

        if (keyword != null && !keyword.isBlank()) {
            boards =
                    boardRepository
                            .findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(keyword);

        } else if (writer != null && !writer.isBlank()) {
            boards =
                    boardRepository
                            .findByWriterContainingIgnoreCaseOrderByCreatedAtDesc(writer);

        } else {
            boards =
                    boardRepository.findAllByOrderByCreatedAtDesc();
        }

        return boards.stream()
                .map(BoardResponse::from)
                .toList();
    }

    // 게시글 상세 조회
    @Transactional
    public BoardResponse getBoard(Long id) {

        Board board = findBoard(id);

        board.setViewCount(board.getViewCount() + 1);

        return BoardResponse.from(board);
    }

    // 게시글 작성
    @Transactional
    public BoardResponse createBoard(
            BoardRequest request,
            String username
    ) {

        Board board = Board.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .writer(username)
                .build();

        Board savedBoard = boardRepository.save(board);

        return BoardResponse.from(savedBoard);
    }

    // 게시글 수정
    @Transactional
    public BoardResponse updateBoard(
            Long id,
            BoardRequest request,
            String username,
            boolean isAdmin
    ) {

        Board board = findBoard(id);

        checkPermission(board, username, isAdmin);

        board.setTitle(request.getTitle());
        board.setContent(request.getContent());

        return BoardResponse.from(board);
    }

    // 게시글 삭제
    @Transactional
    public void deleteBoard(
            Long id,
            String username,
            boolean isAdmin
    ) {

        Board board = findBoard(id);

        checkPermission(board, username, isAdmin);

        boardRepository.delete(board);
    }

    // 게시글 찾기
    private Board findBoard(Long id) {

        return boardRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "게시글을 찾을 수 없습니다."
                        )
                );
    }

    // 작성자 또는 ADMIN 권한 확인
    private void checkPermission(
            Board board,
            String username,
            boolean isAdmin
    ) {

        if (!board.getWriter().equals(username) && !isAdmin) {
            throw new IllegalArgumentException(
                    "게시글을 수정하거나 삭제할 권한이 없습니다."
            );
        }
    }
}