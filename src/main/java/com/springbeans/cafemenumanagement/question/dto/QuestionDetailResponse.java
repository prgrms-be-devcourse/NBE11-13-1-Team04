package com.springbeans.cafemenumanagement.question.dto;

import com.springbeans.cafemenumanagement.answer.dto.AnswerDetailResponse;
import com.springbeans.cafemenumanagement.question.domain.entity.Question;
import com.springbeans.cafemenumanagement.question.domain.entity.QuestionStatus;

import java.time.LocalDateTime;

public record QuestionDetailResponse(
        Long id,
        String title,
        String content,
        QuestionStatus status,
        LocalDateTime createdAt,
        AnswerDetailResponse answer
) {

    public static QuestionDetailResponse from(
            Question question,
            AnswerDetailResponse answer
    ) {
        return new QuestionDetailResponse(
                question.getId(),
                question.getTitle(),
                question.getContent(),
                question.getStatus(),
                question.getCreatedAt(),
                answer
        );
    }
}