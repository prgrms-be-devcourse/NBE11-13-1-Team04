package com.springbeans.cafemenumanagement.admin.sales.exception;

import com.springbeans.cafemenumanagement.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SalesErrorCode implements ErrorCode {

    INVALID_DATE_RANGE(HttpStatus.BAD_REQUEST, "SALES_001", "유효하지 않은 연도 또는 월 입력입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}