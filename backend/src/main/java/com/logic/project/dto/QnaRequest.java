package com.logic.project.dto;

import com.logic.project.domain.Qna;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QnaRequest {

    @NotNull(message = "문의 유형을 선택해주세요.")
    private Qna.Category category;

    @NotBlank(message = "제목을 입력해주세요.")
    @Size(max = 200, message = "제목은 200자 이하로 입력해주세요.")
    private String title;

    @NotBlank(message = "문의 내용을 입력해주세요.")
    private String content;

    private boolean secret;
}
