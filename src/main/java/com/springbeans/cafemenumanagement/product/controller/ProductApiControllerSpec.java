package com.springbeans.cafemenumanagement.product.controller;

import com.springbeans.cafemenumanagement.product.dto.ProductResponse;
import com.springbeans.cafemenumanagement.product.dto.ProductSaveRequest;
import com.springbeans.cafemenumanagement.product.dto.ProductSaveResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

public interface ProductApiControllerSpec {
    @Operation(summary = "상품 단건 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "상품 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema =  @Schema(implementation = ProductResponse.class)
                    )
            ),
            @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/{id}")
    ResponseEntity<ProductResponse> getProduct(@Parameter(description = "조회할 상품 ID", example = "1") @PathVariable Long id);

    @Operation(summary = "상품 전체 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "전체 상품 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ProductResponse.class)
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping
    ResponseEntity<List<ProductResponse>> getAllProducts() throws IOException;

    @Operation(summary = "상품 카테고리 조회")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "카테고리에 맞는 상품 조회 성공",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(
                                    schema = @Schema(implementation = ProductResponse.class)
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
    })
    @GetMapping("/{category}")
    ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @Parameter(description = "조회할 상품 카테고리", example = "커피 원두") @PathVariable String category
    );
}
