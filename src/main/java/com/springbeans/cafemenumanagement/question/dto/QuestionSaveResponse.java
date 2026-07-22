package com.springbeans.cafemenumanagement.question.dto;

import com.springbeans.cafemenumanagement.question.domain.entity.Question;
import com.springbeans.cafemenumanagement.question.domain.entity.QuestionStatus;

import java.time.LocalDateTime;

public record QuestionSaveResponse(
        Long id,
        String title,
        QuestionStatus status,
        LocalDateTime createdAt
) {

    public static QuestionSaveResponse from(Question question) {
        return new QuestionSaveResponse(
                question.getId(),
                question.getTitle(),
                question.getStatus(),
                question.getCreatedAt()
        );
    }
}