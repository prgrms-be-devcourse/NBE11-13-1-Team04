package com.springbeans.cafemenumanagement.question.exception;

import com.springbeans.cafemenumanagement.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum QuestionErrorCode implements ErrorCode {

    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "QUESTION_001", "존재하지 않는 문의입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}