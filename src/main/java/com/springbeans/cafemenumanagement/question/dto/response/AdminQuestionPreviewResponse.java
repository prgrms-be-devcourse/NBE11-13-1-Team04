package com.springbeans.cafemenumanagement.question.dto.response;

import com.springbeans.cafemenumanagement.question.domain.entity.Question;
import com.springbeans.cafemenumanagement.question.domain.entity.QuestionStatus;

import java.time.LocalDateTime;

public record AdminQuestionPreviewResponse(
        Long id,
        String email,
        String title,
        QuestionStatus status,
        LocalDateTime createdAt
) {

    public static AdminQuestionPreviewResponse from(Question question) {
        return new AdminQuestionPreviewResponse(
                question.getId(),
                question.getEmail(),
                question.getTitle(),
                question.getStatus(),
                question.getCreatedAt()
        );
    }
}