package com.springbeans.cafemenumanagement.product.controller;

import com.springbeans.cafemenumanagement.product.dto.request.ProductSaveRequest;
import com.springbeans.cafemenumanagement.product.dto.response.ProductSaveResponse;
import com.springbeans.cafemenumanagement.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductApiController {
    @Autowired
    private ProductService productService;

    @Operation(summary = "상품 등록", description = "상품 정보를 등록합니다.")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "201",
                            description = "상품 등록 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductSaveResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "요청값 검증 실패"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductSaveResponse> addProduct(@Valid @ModelAttribute ProductSaveRequest request ) throws IOException {
        return productService.save(request);
    }

    @Operation(summary = "상품 수정")
    @ApiResponses(
            {
                    @ApiResponse(
                            responseCode = "200",
                            description = "상품 수정 성공",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = ProductSaveResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ProductSaveResponse> updateProduct( @PathVariable Long id, @Valid @ModelAttribute ProductSaveRequest request ) throws IOException {
        return productService.update(id, request);
    }

    @Operation(summary = "상품 삭제")
    @ApiResponses(
            {
                    @ApiResponse(responseCode = "204", description = "상품 삭제 성공"),
                    @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct( @PathVariable Long id ) throws IOException {
        return productService.delete(id);
    }
}
