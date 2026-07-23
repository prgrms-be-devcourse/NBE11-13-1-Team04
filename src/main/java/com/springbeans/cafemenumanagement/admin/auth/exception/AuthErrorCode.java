package com.springbeans.cafemenumanagement.admin.auth.exception;

import com.springbeans.cafemenumanagement.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH_001", "아이디 또는 비밀번호가 일치하지 않습니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "AUTH_002", "관리자 로그인이 필요합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}