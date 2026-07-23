package com.springbeans.cafemenumanagement.global.exception;

import com.springbeans.cafemenumanagement.global.slack.SlackNotificationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final SlackNotificationService slackNotificationService;

    // 1. 비즈니스 예외 처리
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiErrorResponse.from(errorCode));
    }

    // 2. @Valid 검증 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidException(MethodArgumentNotValidException exception) {
        log.error("Validation Error", exception);

        Map<String, String> fieldErrors = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> Objects.requireNonNullElse(
                                error.getDefaultMessage(),
                                "잘못된 요청입니다."
                                                           ),
                        (first, second) -> first, LinkedHashMap::new
                                         ));
        ApiErrorResponse response = ApiErrorResponse.validation(
                "COMMON_001",
                "입력값이 올바르지 않습니다.",
                fieldErrors
                                                               );
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    // 3. 파라미터 타입 불일치 예외 처리
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        log.error("MethodArgumentTypeMismatchException", exception);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiErrorResponse.validation(exception.getErrorCode(), exception.getMessage(), Map.ofEntries()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNoResourceFoundException( NoResourceFoundException exception ) {
        log.warn("No static resource found: {}", exception.getMessage());
        return ResponseEntity.notFound().build();

    }

    // 4. 예상치 못한 500 서버 내부 에러 처리 (★ 슬랙 알림 발송)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception exception, HttpServletRequest request) {
        log.error("Unhandled Exception occurred: ", exception);

        // 스택 트레이스를 문자열로 변환
        StringWriter sw = new StringWriter();
        exception.printStackTrace(new PrintWriter(sw));
        String stackTrace = sw.toString();

        // 슬랙 알림 발송 (요청 API 경로, 에러 메시지, 스택 트레이스)
        slackNotificationService.sendErrorNotification(
                request.getRequestURI(),
                exception.getMessage() != null ? exception.getMessage() : "NullPointerException / Unhandled Error",
                stackTrace
                                                      );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiErrorResponse.internalServerError());
    }
}