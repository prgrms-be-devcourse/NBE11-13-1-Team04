package com.springbeans.cafemenumanagement.question.exception;

import com.springbeans.cafemenumanagement.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum QuestionErrorCode implements ErrorCode {

    QUESTION_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "QUESTION_001",
            "문의를 찾을 수 없습니다."
    ),

    QUESTION_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "QUESTION_002",
            "문의 조회 권한이 없습니다."
    ),

    QUESTION_DELETE_ACCESS_DENIED(
            HttpStatus.FORBIDDEN,
            "QUESTION_003",
            "문의 삭제 권한이 없습니다."
    ),

    QUESTION_REQUEST_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "QUESTION_004",
            "문의 등록 요청은 필수입니다."
    ),

    QUESTION_EMAIL_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "QUESTION_005",
            "이메일은 필수입니다."
    ),

    QUESTION_TITLE_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "QUESTION_006",
            "제목은 필수입니다."
    ),

    QUESTION_CONTENT_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "QUESTION_007",
            "문의 내용은 필수입니다."
    ),

    QUESTION_AUTH_REQUEST_REQUIRED(
            HttpStatus.BAD_REQUEST,
            "QUESTION_008",
            "문의 인증 요청은 필수입니다."
    ),

    QUESTION_ALREADY_DELETED(
            HttpStatus.CONFLICT,
            "QUESTION_009",
            "이미 삭제된 문의입니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}