package com.springbeans.cafemenumanagement.answer.dto;

import com.springbeans.cafemenumanagement.answer.domain.entity.Answer;

import java.time.LocalDateTime;

public record AnswerSaveResponse(
        Long id,
        Long questionId,
        String content,
        LocalDateTime createdAt
) {

    public static AnswerSaveResponse from(Answer answer) {
        return new AnswerSaveResponse(
                answer.getId(),
                answer.getQuestion().getId(),
                answer.getContent(),
                answer.getCreatedAt()
        );
    }
}