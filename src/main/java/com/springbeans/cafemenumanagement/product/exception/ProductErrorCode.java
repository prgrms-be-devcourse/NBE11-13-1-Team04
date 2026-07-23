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

    INVALID_CATEGORY(
            HttpStatus.BAD_REQUEST,
            "PRODUCT_003",
            "카테고리가 올바르지 않습니다."
    ),

    IMAGE_FILENAME_MISSING(
            HttpStatus.BAD_REQUEST,
            "PRODUCT_004",
            "이미지 파일명이 존재하지 않습니다."
    ),

    IMAGE_EXTENSION_MISSING(
            HttpStatus.BAD_REQUEST,
            "PRODUCT_005",
            "이미지 파일 확장자가 존재하지 않습니다."
    ),

    UNSUPPORTED_IMAGE_EXTENSION(
            HttpStatus.BAD_REQUEST,
            "PRODUCT_006",
            "지원하지 않는 이미지 파일 형식입니다."
    ),

    INVALID_IMAGE_PATH(
            HttpStatus.BAD_REQUEST,
            "PRODUCT_007",
            "잘못된 이미지 파일 경로입니다."
    ),

    IMAGE_DIRECTORY_CREATE_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "PRODUCT_008",
            "이미지 저장 디렉터리 생성에 실패했습니다."
    ),

    IMAGE_SAVE_FAILED(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "PRODUCT_009",
            "이미지 파일 저장에 실패했습니다."
    );

    private final HttpStatus status;
    private final String code;
    private final String message;
}
