package com.springbeans.cafemenumanagement.global.exception;


import java.util.Map;

// 에러 ResponseDto
// 기존에 springframework.web에 ErrorResponse가 있어 이름을 변경했습니다.
public record ApiErrorResponse(
        String code,
        String message,
        Map<String, String> FieldErrors // 검증 오류시 필드 반환하기 위함
) {
    // 기본
    public static ApiErrorResponse from(ErrorCode errorCode) {
        return new ApiErrorResponse(
                errorCode.getCode(),
                errorCode.getMessage(),
                Map.of()
        );
    }

    // 검증 오류 시
    public static ApiErrorResponse validation(String code, String message, Map<String, String> fieldErrors) {
        return new ApiErrorResponse(
                code,
                message,
                fieldErrors
        );
    }

    // 서버 에러
    public static ApiErrorResponse internalServerError() {
        return new ApiErrorResponse(
                "COMMON_500",
                "서버 내부 오류가 발생했습니다.",
                Map.of()
        );
    }
}
