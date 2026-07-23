package com.springbeans.cafemenumanagement.question.dto.request;

public record QuestionCreateRequest(
        String email,
        String title,
        String content
) {
}