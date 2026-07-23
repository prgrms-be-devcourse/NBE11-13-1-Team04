package com.springbeans.cafemenumanagement.question.dto.response;

import com.springbeans.cafemenumanagement.answer.dto.response.AnswerDetailResponse;
import com.springbeans.cafemenumanagement.question.domain.entity.Question;
import com.springbeans.cafemenumanagement.question.domain.entity.QuestionStatus;

import java.time.LocalDateTime;

public record AdminQuestionDetailResponse(
        Long id,
        String email,
        String title,
        String content,
        QuestionStatus status,
        LocalDateTime createdAt,
        AnswerDetailResponse answer
) {

    public static AdminQuestionDetailResponse from(
            Question question,
            AnswerDetailResponse answer
    ) {
        return new AdminQuestionDetailResponse(
                question.getId(),
                question.getEmail(),
                question.getTitle(),
                question.getContent(),
                question.getStatus(),
                question.getCreatedAt(),
                answer
        );
    }
}