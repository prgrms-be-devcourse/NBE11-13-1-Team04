package com.springbeans.cafemenumanagement.admin.product.controller;

import com.springbeans.cafemenumanagement.product.dto.ProductSaveRequest;
import com.springbeans.cafemenumanagement.product.dto.ProductSaveResponse;
import com.springbeans.cafemenumanagement.product.service.ProductService;
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
public class AdminProductApiController implements AdminProductApiControllerSpec {
    @Autowired
    private ProductService productService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductSaveResponse> addProduct(@Valid @ModelAttribute ProductSaveRequest request) throws IOException {
        return productService.save(request);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductSaveResponse> updateProduct(@PathVariable Long id, @Valid @ModelAttribute ProductSaveRequest request) throws IOException {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        return productService.delete(id);
    }
}
