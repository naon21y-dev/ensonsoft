package com.logic.project.service;

import com.logic.project.domain.Qna;
import com.logic.project.dto.QnaAnswerRequest;
import com.logic.project.dto.QnaRequest;
import com.logic.project.dto.QnaResponse;
import com.logic.project.repository.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaService {

    private final QnaRepository qnaRepository;

    // ========================================
    // 전체 조회
    // ========================================

    public List<QnaResponse> findAll() {

        return qnaRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(QnaResponse::from)
                .toList();
    }

    // ========================================
    // 상세 조회
    // ========================================

    public QnaResponse findById(
            Long id,
            String username,
            boolean admin
    ) {

        Qna qna = getQna(id);

        checkReadPermission(
                qna,
                username,
                admin
        );

        return QnaResponse.from(qna);
    }

    // ========================================
    // 내가 작성한 문의
    // ========================================

    public List<QnaResponse> findMyQna(
            String username
    ) {

        return qnaRepository
                .findByUsernameOrderByCreatedAtDesc(username)
                .stream()
                .map(QnaResponse::from)
                .toList();
    }

    // ========================================
    // 상태별 조회
    // ========================================

    public List<QnaResponse> findByStatus(
            Qna.Status status
    ) {

        return qnaRepository
                .findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(QnaResponse::from)
                .toList();
    }

    // ========================================
    // 유형별 조회
    // ========================================

    public List<QnaResponse> findByCategory(
            Qna.Category category
    ) {

        return qnaRepository
                .findByCategoryOrderByCreatedAtDesc(category)
                .stream()
                .map(QnaResponse::from)
                .toList();
    }

    // ========================================
    // 제목 검색
    // ========================================

    public List<QnaResponse> search(
            String keyword
    ) {

        return qnaRepository
                .findByTitleContainingIgnoreCaseOrderByCreatedAtDesc(keyword)
                .stream()
                .map(QnaResponse::from)
                .toList();
    }

    // ========================================
    // 문의 등록
    // ========================================

    @Transactional
    public QnaResponse create(
            QnaRequest request,
            String username
    ) {

        Qna qna = Qna.builder()
                .category(request.getCategory())
                .title(request.getTitle())
                .content(request.getContent())
                .username(username)
                .secret(request.isSecret())
                .status(Qna.Status.WAITING)
                .build();

        return QnaResponse.from(
                qnaRepository.save(qna)
        );
    }

    // ========================================
    // 문의 수정
    // ========================================

    @Transactional
    public QnaResponse update(
            Long id,
            QnaRequest request,
            String username,
            boolean admin
    ) {

        Qna qna = getQna(id);

        checkOwner(
                qna,
                username,
                admin
        );

        qna.setCategory(request.getCategory());
        qna.setTitle(request.getTitle());
        qna.setContent(request.getContent());
        qna.setSecret(request.isSecret());

        return QnaResponse.from(qna);
    }

    // ========================================
    // 문의 삭제
    // ========================================

    @Transactional
    public void delete(
            Long id,
            String username,
            boolean admin
    ) {

        Qna qna = getQna(id);

        checkOwner(
                qna,
                username,
                admin
        );

        qnaRepository.delete(qna);
    }

    // ========================================
    // 관리자 답변 등록 / 수정
    // ========================================

    @Transactional
    public QnaResponse answer(
            Long id,
            QnaAnswerRequest request,
            String adminUsername
    ) {

        Qna qna = getQna(id);

        qna.setAnswer(request.getAnswer());
        qna.setAnsweredBy(adminUsername);
        qna.setAnsweredAt(LocalDateTime.now());
        qna.setStatus(Qna.Status.ANSWERED);

        return QnaResponse.from(qna);
    }

    // ========================================
    // 관리자 답변 삭제
    // ========================================

    @Transactional
    public QnaResponse deleteAnswer(
            Long id
    ) {

        Qna qna = getQna(id);

        qna.setAnswer(null);
        qna.setAnsweredBy(null);
        qna.setAnsweredAt(null);
        qna.setStatus(Qna.Status.WAITING);

        return QnaResponse.from(qna);
    }

    // ========================================
    // 내부 조회
    // ========================================

    private Qna getQna(Long id) {

        return qnaRepository.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "해당 문의를 찾을 수 없습니다."
                        )
                );
    }

    // ========================================
    // 비밀글 조회 권한
    // ========================================

    private void checkReadPermission(
            Qna qna,
            String username,
            boolean admin
    ) {

        if (!qna.isSecret()) {
            return;
        }

        if (admin) {
            return;
        }

        if (!qna.getUsername().equals(username)) {
            throw new AccessDeniedException(
                    "비공개 문의는 작성자와 관리자만 확인할 수 있습니다."
            );
        }
    }

    // ========================================
    // 수정 / 삭제 권한
    // ========================================

    private void checkOwner(
            Qna qna,
            String username,
            boolean admin
    ) {

        if (admin) {
            return;
        }

        if (!qna.getUsername().equals(username)) {
            throw new AccessDeniedException(
                    "본인이 작성한 문의만 수정하거나 삭제할 수 있습니다."
            );
        }
    }
}
