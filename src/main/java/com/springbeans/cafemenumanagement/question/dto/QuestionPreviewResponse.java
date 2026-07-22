package com.springbeans.cafemenumanagement.question.dto;

import com.springbeans.cafemenumanagement.question.domain.entity.Question;
import com.springbeans.cafemenumanagement.question.domain.entity.QuestionStatus;

import java.time.LocalDateTime;

public record QuestionPreviewResponse(
        Long id,
        String title,
        QuestionStatus status,
        LocalDateTime createdAt
) {

    public static QuestionPreviewResponse from(Question question) {
        return new QuestionPreviewResponse(
                question.getId(),
                question.getTitle(),
                question.getStatus(),
                question.getCreatedAt()
        );
    }
}