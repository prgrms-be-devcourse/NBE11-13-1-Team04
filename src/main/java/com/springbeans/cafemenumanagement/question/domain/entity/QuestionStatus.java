package com.springbeans.cafemenumanagement.question.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuestionStatus {

    WAITING("답변 대기"),
    ANSWERED("답변 완료");

    private final String description;
}
