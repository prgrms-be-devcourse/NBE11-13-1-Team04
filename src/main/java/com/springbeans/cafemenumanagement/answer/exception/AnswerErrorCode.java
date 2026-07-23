package com.springbeans.cafemenumanagement.answer.exception;

import com.springbeans.cafemenumanagement.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AnswerErrorCode implements ErrorCode {

    ANSWER_NOT_FOUND(HttpStatus.NOT_FOUND, "ANSWER_001", "존재하지 않는 답변입니다."),
    ANSWER_ALREADY_EXISTS(HttpStatus.CONFLICT, "ANSWER_002", "이미 답변이 등록된 문의입니다."),
    EMPTY_CONTENT(HttpStatus.BAD_REQUEST, "ANSWER_003", "답변 내용은 필수입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}