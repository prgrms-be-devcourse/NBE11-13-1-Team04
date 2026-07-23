package com.springbeans.cafemenumanagement.global.exception;

import org.springframework.http.HttpStatus;

// 에러코드 형식을 지정하는 인터페이스
public interface ErrorCode {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
