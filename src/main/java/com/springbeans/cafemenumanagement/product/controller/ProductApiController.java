package com.springbeans.cafemenumanagement.product.controller;

import com.springbeans.cafemenumanagement.product.dto.response.ProductResponse;
import com.springbeans.cafemenumanagement.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/products")
public class ProductApiController {
    @Autowired
    private ProductService productService;

    @Operation(summary = "상품 단건 조회")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "상품 조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(
            @Parameter(description = "조회할 상품 ID", example = "1")
            @PathVariable Long id
                                                     ) {
        return productService.get(id);
    }

    @Operation(summary = "상품 전체 조회")
    @ApiResponses(
            value = {
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
            }
    )
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() throws IOException {
        try {
            return productService.getAll();
        } catch (Exception e) {
            e.printStackTrace(); // 인텔리제이 콘솔에 진짜 원인 에러 출력
            return ResponseEntity.internalServerError().build(); // 500 Internal Server Error 반환
        }
    }

    @Operation(summary = "상품 카테고리별 조회")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "카테고리별 상품 조회 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = ProductResponse.class)
                                    )
                            )
                    ),
                    @ApiResponse(responseCode = "500", description = "서버 오류", content = @Content)
            }
    )
    @GetMapping("/category")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(
            @RequestParam String category
    ) {
        try {
            return productService.getByCategory(category);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
