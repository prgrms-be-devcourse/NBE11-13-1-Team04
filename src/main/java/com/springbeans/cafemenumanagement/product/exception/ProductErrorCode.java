package com.springbeans.cafemenumanagement.product.exception;

import com.springbeans.cafemenumanagement.global.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    PRODUCT_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "PRODUCT_001",
            "상품을 찾을 수 없습니다."
    ),

    PRODUCT_ALREADY_DELETED(
            HttpStatus.CONFLICT,
            "PRODUCT_002",
            "이미 삭제된 상품입니다."
    ),

    PRODUCT_NAME_DUPLICATED(
            HttpStatus.CONFLICT,
            "PRODUCT_003",
            "이미 존재하는 상품명입니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
