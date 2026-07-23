package com.springbeans.cafemenumanagement.product.exception;

import com.springbeans.cafemenumanagement.global.exception.BusinessException;

public class ProductException extends BusinessException {
    public ProductException(ProductErrorCode errorCode) {
        super(errorCode);
    }

    public ProductException(ProductErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
