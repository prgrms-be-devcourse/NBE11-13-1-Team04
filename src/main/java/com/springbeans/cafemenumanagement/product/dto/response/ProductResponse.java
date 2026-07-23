package com.springbeans.cafemenumanagement.product.dto.response;

import com.springbeans.cafemenumanagement.product.entity.Product;
import io.swagger.v3.oas.annotations.media.Schema;

public record ProductResponse(

        @Schema(description = "상품 ID", example = "1")
        Long id,

        @Schema(description = "상품명", example = "에티오피아 게이샤 100g")
        String name,

        @Schema(description = "상품 가격", example = "40000")
        int price,

        @Schema(description = "상품 종류", example = "커피 원두")
        String category,

        @Schema(description = "상품 이미지 정적 리소스 경로", example = "c83b04fd-8f41-4163-a593-ff9604e7861f.jpg")
        String filePath,

        @Schema(description = "상품 수량", example = "1", defaultValue = "10")
        Integer stock
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getCategory(),
                product.getFilePath(),
                product.getStock()
        );
    }
}