package com.springbeans.cafemenumanagement.question.dto;

public record QuestionCreateRequest(
        String email,
        String title,
        String content
) {
}