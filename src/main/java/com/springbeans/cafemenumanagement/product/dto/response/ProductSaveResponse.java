package com.springbeans.cafemenumanagement.product.dto.response;

import com.springbeans.cafemenumanagement.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ProductSaveResponse(

        @Schema(description = "상품 ID")
        Long id,

        @Schema(description = "상품명")
        String name,

        @Schema(description = "상품 가격")
        int price,

        @Schema(description = "상품 종류")
        String category,

        @Schema(description = "상품 이미지 정적 리소스 경로")
        String filePath,

        @Schema(description = "상품 정보 최초 생성일")
        LocalDateTime createdAt,

        @Schema(description = "상품 정보 최근 수정일")
        LocalDateTime updatedAt,

        @Schema(description = "상품 수량", example = "1", defaultValue = "10")
        Integer stock
){
    public static ProductSaveResponse from(Product product){
        return new ProductSaveResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory(),
                product.getFilePath(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getStock()
        );
    }
}
