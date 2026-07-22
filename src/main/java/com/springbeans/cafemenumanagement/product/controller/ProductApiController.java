package com.springbeans.cafemenumanagement.product.controller;

import com.springbeans.cafemenumanagement.product.dto.ProductResponse;
import com.springbeans.cafemenumanagement.product.dto.ProductSaveRequest;
import com.springbeans.cafemenumanagement.product.dto.ProductSaveResponse;
import com.springbeans.cafemenumanagement.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("api/products")
public class ProductApiController implements ProductApiControllerSpec {
    @Autowired
    private ProductService productService;

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        return productService.get(id);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() throws IOException {
        try {
            return productService.getAll();
        } catch (Exception e) {
            e.printStackTrace(); // 인텔리제이 콘솔에 진짜 원인 에러 출력
            return ResponseEntity.internalServerError().build(); // 500 Internal Server Error 반환
        }
    }

    @GetMapping("category/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        return productService.getByCategory(category);
    @Operation(summary = "상품 수정")
    
    @PostMapping("/{id}")
    public ResponseEntity<ProductSaveResponse> updateProduct( @PathVariable Long id, @Valid @ModelAttribute ProductSaveRequest request ) throws IOException {
        return productService.update(id, request);
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct( @PathVariable Long id ) throws IOException {
        return productService.delete(id);
    }
}
