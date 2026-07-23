package com.springbeans.cafemenumanagement.order.exception;

import com.springbeans.cafemenumanagement.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements ErrorCode {

    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "ORDER_001", "존재하지 않는 주문입니다."),
    INVALID_CANCEL_REQUEST_STATUS(HttpStatus.BAD_REQUEST, "ORDER_002", "취소 요청 상태의 주문만 승인할 수 있습니다."),
    CANNOT_CANCEL_CONFIRMED_ORDER(HttpStatus.BAD_REQUEST, "ORDER_003", "확정된 주문은 취소할 수 없습니다."),
    NOT_CANCELABLE_STATUS(HttpStatus.BAD_REQUEST, "ORDER_004", "취소할 수 없는 주문 상태입니다."); // 👈 추가

    private final HttpStatus status;
    private final String code;
    private final String message;
}