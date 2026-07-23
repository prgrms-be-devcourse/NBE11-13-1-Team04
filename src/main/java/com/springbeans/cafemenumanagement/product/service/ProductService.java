package com.springbeans.cafemenumanagement.product.service;

import com.springbeans.cafemenumanagement.product.dto.request.ProductSaveRequest;
import com.springbeans.cafemenumanagement.product.dto.response.ProductResponse;
import com.springbeans.cafemenumanagement.product.dto.response.ProductSaveResponse;
import com.springbeans.cafemenumanagement.product.entity.Product;
import com.springbeans.cafemenumanagement.product.exception.ProductErrorCode;
import com.springbeans.cafemenumanagement.product.exception.ProductException;
import com.springbeans.cafemenumanagement.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private static final Path PRODUCT_IMAGE_ROOT =
            Paths.get("./uploads/products")
                    .toAbsolutePath()
                    .normalize();
    private static final String PRODUCT_IMAGE_URL =
            "/images/products/";

    @Transactional
    public ResponseEntity<ProductSaveResponse> save(ProductSaveRequest req) {
        try {
            Files.createDirectories(PRODUCT_IMAGE_ROOT);
        } catch (IOException e) {
            throw new ProductException(ProductErrorCode.IMAGE_DIRECTORY_CREATE_FAILED);
        }


        String fileName = "";
        Path savePath = null;
        if (req.image() != null && !req.image().isEmpty()) {
            fileName = UUID.randomUUID() + getExtension(req.image().getOriginalFilename());
            savePath = PRODUCT_IMAGE_ROOT
                    .resolve(fileName)
                    .normalize();
        }

        if (savePath != null && !savePath.startsWith(PRODUCT_IMAGE_ROOT)) {
            throw new ProductException(ProductErrorCode.INVALID_IMAGE_PATH);
        }

        // transferTo(): MultipartFile에서 지원하는 메서드, 데이터를 메모리에 로드하지 않고 디스크에 전송
        if (savePath != null) {
            try {
                req.image().transferTo(savePath);
            } catch (IOException e) {
                throw new ProductException(ProductErrorCode.IMAGE_SAVE_FAILED);
            }
        };

         Product product = productRepository.save(Product.builder()
                 .name(req.name())
                 .price(req.price())
                 .category(req.category())
                 .filePath(PRODUCT_IMAGE_URL + fileName)
                 .createdAt(LocalDateTime.now())
                 .updatedAt(LocalDateTime.now())
                 .stock(req.stock())
                 .build());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProductSaveResponse.from(product));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<ProductResponse> get(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return ResponseEntity.status(HttpStatus.OK)
                .body(ProductResponse.from(product));
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<ProductResponse>> getAll(){
        List<ProductResponse> products = productRepository.findByIsActiveTrue().stream()
                .sorted(Comparator.comparing(Product::getId))
                .map(ProductResponse::from)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<List<ProductResponse>> getByCategory(String category){
        if (category == null || category.isBlank()) { throw new ProductException(ProductErrorCode.INVALID_CATEGORY); }
        List<ProductResponse> products = productRepository.findByCategoryAndIsActiveTrue(category)
                .stream()
                .sorted(Comparator.comparing(Product::getId))
                .map(ProductResponse::from)
                .toList();
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

    @Transactional
    public ResponseEntity<ProductSaveResponse> update(Long id, ProductSaveRequest req) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                        new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND)
                );

        String filePath = product.getFilePath();
        if (req.image() != null && !req.image().isEmpty()) {
            String fileName = UUID.randomUUID() + getExtension(req.image().getOriginalFilename());
            Path newPath = PRODUCT_IMAGE_ROOT
                    .resolve(fileName)
                    .normalize();
            Files.createDirectories(PRODUCT_IMAGE_ROOT);
            req.image().transferTo(newPath);
            filePath = PRODUCT_IMAGE_URL + fileName;
        }

        product.update(
                req.name(),
                req.price(),
                req.category(),
                filePath,
                product.isActive(),
                req.stock()
        );

        return ResponseEntity.status(HttpStatus.OK).body(ProductSaveResponse.from(product));
    }

    @Transactional
    public ResponseEntity<Void> delete(Long id){
        Product product = productRepository.findById(id)
                .orElseThrow(() ->
                    new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND)
                );
        if (!product.isActive()) { throw new ProductException(ProductErrorCode.PRODUCT_ALREADY_DELETED); }

        product.update(
                product.getName(),
                product.getPrice(),
                product.getCategory(),
                product.getFilePath(),
                false,
                product.getStock()
        );

        return ResponseEntity.noContent().build();
    }

    // 원본 파일의 확장자만 분리하는 메서드 (coffee.jpg -> .jpg)
    private String getExtension(String originalFilename){
        if (originalFilename == null || originalFilename.isBlank()) throw new ProductException(ProductErrorCode.IMAGE_FILENAME_MISSING);
        String fileName = Paths.get(originalFilename).getFileName().toString();
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex == -1) throw new ProductException(ProductErrorCode.IMAGE_EXTENSION_MISSING);
        return fileName.substring(dotIndex).toLowerCase();
    }
}
