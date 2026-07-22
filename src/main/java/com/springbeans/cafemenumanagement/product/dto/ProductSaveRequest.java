package com.springbeans.cafemenumanagement.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.web.multipart.MultipartFile;

public record ProductSaveRequest(

        @Schema(description = "상품명", example = "에티오피아 게이샤 100g")
        @NotBlank(message = "상품명은 필수입니다.")
        @Size(max = 50, message = "상품명은 50자 이하여야 합니다.")
        String name,

        @Schema(description = "상품 가격", example = "40000")
        @NotNull(message = "가격은 필수입니다.")
        @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
        Integer price,

        @Schema(description = "상품 종류", example = "커피 원두")
        @NotBlank(message = "카테고리는 필수입니다.")
        @Size(max = 50, message = "카테고리는 50자 이하여야 합니다.")
        String category,

        @Schema(description = "상품 대표 이미지", type = "string", format = "binary")
        @NotNull(message = "상품 이미지는 필수입니다.")
        MultipartFile image,

        @Schema(description = "상품 수량", example = "1")
        @Min(value = 0, message = "재고는 0개 이상이어야 합니다.")
        int stock
){
}
