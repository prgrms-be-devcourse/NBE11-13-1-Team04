package com.springbeans.cafemenumanagement.answer.dto;

import com.springbeans.cafemenumanagement.answer.domain.entity.Answer;

import java.time.LocalDateTime;

public record AnswerDetailResponse(
        Long id,
        String content,
        LocalDateTime createdAt
) {

    public static AnswerDetailResponse from(Answer answer) {
        return new AnswerDetailResponse(
                answer.getId(),
                answer.getContent(),
                answer.getCreatedAt()
        );
    }
}