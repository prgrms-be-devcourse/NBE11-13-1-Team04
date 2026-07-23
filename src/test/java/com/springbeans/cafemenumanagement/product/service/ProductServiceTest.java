package com.springbeans.cafemenumanagement.product.service;

import com.springbeans.cafemenumanagement.product.dto.response.ProductResponse;
import com.springbeans.cafemenumanagement.product.dto.request.ProductSaveRequest;
import com.springbeans.cafemenumanagement.product.dto.response.ProductSaveResponse;
import com.springbeans.cafemenumanagement.product.entity.Product;
import com.springbeans.cafemenumanagement.product.exception.ProductException;
import com.springbeans.cafemenumanagement.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MultipartFile image;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepository);
    }

    @Nested
    @DisplayName("상품 등록")
    class SaveProduct {

        @Test
        @DisplayName("상품 정보와 이미지가 정상적으로 전달되면 상품을 등록한다")
        void saveSuccess() throws IOException {
            // given
            ProductSaveRequest request = new ProductSaveRequest(
                    "아메리카노",
                    4500,
                    "커피",
                    image,
                    10
            );

            given(image.getOriginalFilename())
                    .willReturn("americano.jpg");

            doNothing()
                    .when(image)
                    .transferTo(any(Path.class));

            given(productRepository.save(any(Product.class)))
                    .willAnswer(invocation ->
                            invocation.getArgument(0)
                    );

            // when
            ResponseEntity<ProductSaveResponse> response =
                    productService.save(request);

            // then
            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.CREATED);

            assertThat(response.getBody())
                    .isNotNull();

            ArgumentCaptor<Product> productCaptor =
                    ArgumentCaptor.forClass(Product.class);

            then(productRepository)
                    .should()
                    .save(productCaptor.capture());

            Product savedProduct = productCaptor.getValue();

            assertThat(savedProduct.getName())
                    .isEqualTo("아메리카노");

            assertThat(savedProduct.getPrice())
                    .isEqualTo(4500);

            assertThat(savedProduct.getCategory())
                    .isEqualTo("커피");

            assertThat(savedProduct.getStock())
                    .isEqualTo(10);

            assertThat(savedProduct.getFilePath())
                    .startsWith("/images/products/")
                    .endsWith(".jpg");

            assertThat(savedProduct.isActive())
                    .isTrue();

            assertThat(savedProduct.getCreatedAt())
                    .isNotNull();

            assertThat(savedProduct.getUpdatedAt())
                    .isNotNull();

            then(image)
                    .should()
                    .transferTo(any(Path.class));
        }

        @Test
        @DisplayName("원본 파일명이 없으면 상품 등록에 실패한다")
        void saveFailWhenFilenameIsNull() {
            // given
            ProductSaveRequest request = new ProductSaveRequest(
                    "아메리카노",
                    4500,
                    "커피",
                    image,
                    1
            );

            given(image.getOriginalFilename())
                    .willReturn(null);

            // when & then
            assertThatThrownBy(() ->
                    productService.save(request)
            )
                    .isInstanceOf(ProductException.class)
                    .hasMessage("이미지 파일명이 존재하지 않습니다.");

            then(productRepository)
                    .should(never())
                    .save(any(Product.class));
        }

        @Test
        @DisplayName("원본 파일명이 공백이면 상품 등록에 실패한다")
        void saveFailWhenFilenameIsBlank() {
            // given
            ProductSaveRequest request = new ProductSaveRequest(
                    "아메리카노",
                    4500,
                    "커피",
                    image,
                    1
            );

            given(image.getOriginalFilename())
                    .willReturn(" ");

            // when & then
            assertThatThrownBy(() ->
                    productService.save(request)
            )
                    .isInstanceOf(ProductException.class)
                    .hasMessage("이미지 파일명이 존재하지 않습니다.");

            then(productRepository)
                    .should(never())
                    .save(any(Product.class));
        }

        @Test
        @DisplayName("파일 확장자가 없으면 상품 등록에 실패한다")
        void saveFailWhenExtensionDoesNotExist() {
            // given
            ProductSaveRequest request = new ProductSaveRequest(
                    "아메리카노",
                    4500,
                    "커피",
                    image,
                    1
            );

            given(image.getOriginalFilename())
                    .willReturn("americano");

            // when & then
            assertThatThrownBy(() ->
                    productService.save(request)
            )
                    .isInstanceOf(ProductException.class)
                    .hasMessage("이미지 파일 확장자가 존재하지 않습니다.");

            then(productRepository)
                    .should(never())
                    .save(any(Product.class));
        }
    }

    @Nested
    @DisplayName("상품 단건 조회")
    class GetProduct {

        @Test
        @DisplayName("상품 ID가 존재하면 상품 정보를 반환한다")
        void getSuccess() {
            // given
            Product product = createProduct(
                    1L,
                    "아메리카노",
                    4500,
                    "커피",
                    10,
                    true
            );

            given(productRepository.findById(1L))
                    .willReturn(Optional.of(product));

            // when
            ResponseEntity<ProductResponse> response =
                    productService.get(1L);

            // then
            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            ProductResponse body = response.getBody();

            assertThat(body)
                    .isNotNull();

            assertThat(body.id())
                    .isEqualTo(1L);

            assertThat(body.name())
                    .isEqualTo("아메리카노");

            assertThat(body.price())
                    .isEqualTo(4500);

            assertThat(body.category())
                    .isEqualTo("커피");

            assertThat(body.stock())
                    .isEqualTo(10);

            then(productRepository)
                    .should()
                    .findById(1L);
        }

        @Test
        @DisplayName("상품 ID가 존재하지 않으면 예외가 발생한다")
        void getFailWhenProductDoesNotExist() {
            // given
            given(productRepository.findById(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    productService.get(999L)
            )
                    .isInstanceOf(ProductException.class)
                    .hasMessage("상품을 찾을 수 없습니다.");

            then(productRepository)
                    .should()
                    .findById(999L);
        }
    }

    @Nested
    @DisplayName("전체 상품 조회")
    class GetAllProducts {

        @Test
        @DisplayName("활성화된 전체 상품을 ID 순서대로 반환한다")
        void getAllSuccess() {
            // given
            Product firstProduct = createProduct(
                    1L,
                    "아메리카노",
                    4500,
                    "커피",
                    10,
                    true
            );

            Product secondProduct = createProduct(
                    2L,
                    "카페라떼",
                    5000,
                    "커피",
                    5,
                    true
            );

            given(productRepository.findByIsActiveTrue())
                    .willReturn(
                            List.of(
                                    firstProduct,
                                    secondProduct
                            )
                    );

            // when
            ResponseEntity<List<ProductResponse>> response =
                    productService.getAll();

            // then
            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            assertThat(response.getBody())
                    .hasSize(2);

            assertThat(response.getBody())
                    .extracting(ProductResponse::id)
                    .containsExactly(1L, 2L);

            assertThat(response.getBody())
                    .extracting(ProductResponse::name)
                    .containsExactly(
                            "아메리카노",
                            "카페라떼"
                    );

            assertThat(response.getBody())
                    .extracting(ProductResponse::stock)
                    .containsExactly(10, 5);

            then(productRepository)
                    .should()
                    .findByIsActiveTrue();
        }

        @Test
        @DisplayName("활성화된 상품이 없으면 빈 목록을 반환한다")
        void getAllReturnsEmptyList() {
            // given
            given(productRepository.findByIsActiveTrue())
                    .willReturn(List.of());

            // when
            ResponseEntity<List<ProductResponse>> response =
                    productService.getAll();

            // then
            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            assertThat(response.getBody())
                    .isNotNull()
                    .isEmpty();

            then(productRepository)
                    .should()
                    .findByIsActiveTrue();
        }
    }

    @Nested
    @DisplayName("카테고리별 상품 조회")
    class GetProductsByCategory {

        @Test
        @DisplayName("카테고리에 해당하는 상품을 ID 순서대로 반환한다")
        void getByCategorySuccess() {
            // given
            Product secondProduct = createProduct(
                    2L,
                    "카페라떼",
                    5000,
                    "커피",
                    5,
                    true
            );

            Product firstProduct = createProduct(
                    1L,
                    "아메리카노",
                    4500,
                    "커피",
                    10,
                    true
            );

            given(productRepository.findByCategoryAndIsActiveTrue("커피"))
                    .willReturn(
                            List.of(
                                    secondProduct,
                                    firstProduct
                            )
                    );

            // when
            ResponseEntity<List<ProductResponse>> response =
                    productService.getByCategory("커피");

            // then
            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            assertThat(response.getBody())
                    .hasSize(2);

            assertThat(response.getBody())
                    .extracting(ProductResponse::id)
                    .containsExactly(1L, 2L);

            assertThat(response.getBody())
                    .extracting(ProductResponse::category)
                    .containsOnly("커피");

            then(productRepository)
                    .should()
                    .findByCategoryAndIsActiveTrue("커피");
        }

        @Test
        @DisplayName("카테고리에 해당하는 상품이 없으면 빈 목록을 반환한다")
        void getByCategoryReturnsEmptyList() {
            // given
            given(productRepository.findByCategoryAndIsActiveTrue("디저트"))
                    .willReturn(List.of());

            // when
            ResponseEntity<List<ProductResponse>> response =
                    productService.getByCategory("디저트");

            // then
            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            assertThat(response.getBody())
                    .isNotNull()
                    .isEmpty();

            then(productRepository)
                    .should()
                    .findByCategoryAndIsActiveTrue("디저트");
        }

        @Test
        @DisplayName("카테고리가 null이면 예외가 발생한다")
        void getByCategoryFailWhenCategoryIsNull() {
            // when & then
            assertThatThrownBy(() ->
                    productService.getByCategory(null)
            )
                    .isInstanceOf(ProductException.class)
                    .hasMessage("카테고리가 올바르지 않습니다.");

            then(productRepository)
                    .should(never())
                    .findByCategoryAndIsActiveTrue(anyString());
        }

        @Test
        @DisplayName("카테고리가 공백이면 예외가 발생한다")
        void getByCategoryFailWhenCategoryIsBlank() {
            // when & then
            assertThatThrownBy(() ->
                    productService.getByCategory(" ")
            )
                    .isInstanceOf(ProductException.class)
                    .hasMessage("카테고리가 올바르지 않습니다.");

            then(productRepository)
                    .should(never())
                    .findByCategoryAndIsActiveTrue(anyString());
        }
    }

    @Nested
    @DisplayName("상품 수정")
    class UpdateProduct {

        @Test
        @DisplayName("새 이미지가 없으면 기존 이미지 경로를 유지한 채 상품을 수정한다")
        void updateSuccessWithoutImage() throws IOException {
            // given
            Product product = createProduct(
                    1L,
                    "아메리카노",
                    4500,
                    "커피",
                    10,
                    true
            );

            ProductSaveRequest request = new ProductSaveRequest(
                    "아이스 아메리카노",
                    5000,
                    "커피",
                    null,
                    1
            );

            given(productRepository.findById(1L))
                    .willReturn(Optional.of(product));

            String oldFilePath = product.getFilePath();
            LocalDateTime oldUpdatedAt = product.getUpdatedAt();

            // when
            ResponseEntity<ProductSaveResponse> response =
                    productService.update(1L, request);

            // then
            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            assertThat(response.getBody())
                    .isNotNull();

            assertThat(product.getName())
                    .isEqualTo("아이스 아메리카노");

            assertThat(product.getPrice())
                    .isEqualTo(5000);

            assertThat(product.getCategory())
                    .isEqualTo("커피");

            assertThat(product.getFilePath())
                    .isEqualTo(oldFilePath);

            assertThat(product.isActive())
                    .isTrue();

            assertThat(product.getUpdatedAt())
                    .isAfterOrEqualTo(oldUpdatedAt);

            then(productRepository)
                    .should()
                    .findById(1L);

            then(productRepository)
                    .should(never())
                    .save(any(Product.class));
        }

        @Test
        @DisplayName("새 이미지가 있으면 이미지 경로를 변경하고 상품을 수정한다")
        void updateSuccessWithImage() throws IOException {
            // given
            Product product = createProduct(
                    1L,
                    "아메리카노",
                    4500,
                    "커피",
                    10,
                    true
            );

            ProductSaveRequest request = new ProductSaveRequest(
                    "아이스 아메리카노",
                    5000,
                    "커피",
                    image,
                    20
            );

            given(productRepository.findById(1L))
                    .willReturn(Optional.of(product));

            given(image.isEmpty())
                    .willReturn(false);

            given(image.getOriginalFilename())
                    .willReturn("new-americano.png");

            doNothing()
                    .when(image)
                    .transferTo(any(Path.class));

            // when
            ResponseEntity<ProductSaveResponse> response =
                    productService.update(1L, request);

            // then
            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            assertThat(product.getName())
                    .isEqualTo("아이스 아메리카노");

            assertThat(product.getPrice())
                    .isEqualTo(5000);

            assertThat(product.getFilePath())
                    .startsWith("/images/products/")
                    .endsWith(".png");

            then(image)
                    .should()
                    .transferTo(any(Path.class));

            then(productRepository)
                    .should(never())
                    .save(any(Product.class));
        }

        @Test
        @DisplayName("빈 이미지가 전달되면 기존 이미지 경로를 유지한다")
        void updateSuccessWhenImageIsEmpty() throws IOException {
            // given
            Product product = createProduct(
                    1L,
                    "아메리카노",
                    4500,
                    "커피",
                    10,
                    true
            );

            ProductSaveRequest request = new ProductSaveRequest(
                    "아이스 아메리카노",
                    5000,
                    "커피",
                    image,
                    20
            );

            given(productRepository.findById(1L))
                    .willReturn(Optional.of(product));

            given(image.isEmpty())
                    .willReturn(true);

            String oldFilePath = product.getFilePath();

            // when
            ResponseEntity<ProductSaveResponse> response =
                    productService.update(1L, request);

            // then
            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.OK);

            assertThat(product.getFilePath())
                    .isEqualTo(oldFilePath);

            then(image)
                    .should(never())
                    .transferTo(any(Path.class));
        }

        @Test
        @DisplayName("수정할 상품이 존재하지 않으면 예외가 발생한다")
        void updateFailWhenProductDoesNotExist() {
            // given
            ProductSaveRequest request = new ProductSaveRequest(
                    "아이스 아메리카노",
                    5000,
                    "커피",
                    null,
                    20
            );

            given(productRepository.findById(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    productService.update(999L, request)
            )
                    .isInstanceOf(ProductException.class)
                    .hasMessage("상품을 찾을 수 없습니다.");

            then(productRepository)
                    .should()
                    .findById(999L);
        }
    }

    @Nested
    @DisplayName("상품 삭제")
    class DeleteProduct {

        @Test
        @DisplayName("활성화된 상품을 삭제하면 비활성 상태로 변경한다")
        void deleteSuccess() {
            // given
            Product product = createProduct(
                    1L,
                    "아메리카노",
                    4500,
                    "커피",
                    10,
                    true
            );

            given(productRepository.findById(1L))
                    .willReturn(Optional.of(product));

            LocalDateTime oldUpdatedAt = product.getUpdatedAt();

            // when
            ResponseEntity<Void> response =
                    productService.delete(1L);

            // then
            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.NO_CONTENT);

            assertThat(response.getBody())
                    .isNull();

            assertThat(product.isActive())
                    .isFalse();

            assertThat(product.getUpdatedAt())
                    .isAfterOrEqualTo(oldUpdatedAt);

            then(productRepository)
                    .should()
                    .findById(1L);

            then(productRepository)
                    .should(never())
                    .save(any(Product.class));
        }

        @Test
        @DisplayName("삭제할 상품이 존재하지 않으면 예외가 발생한다")
        void deleteFailWhenProductDoesNotExist() {
            // given
            given(productRepository.findById(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    productService.delete(999L)
            )
                    .isInstanceOf(ProductException.class)
                    .hasMessage("상품을 찾을 수 없습니다.");

            then(productRepository)
                    .should()
                    .findById(999L);
        }

        @Test
        @DisplayName("이미 삭제된 상품을 다시 삭제하면 예외가 발생한다")
        void deleteFailWhenProductAlreadyDeleted() {
            // given
            Product product = createProduct(
                    1L,
                    "아메리카노",
                    4500,
                    "커피",
                    10,
                    false
            );

            given(productRepository.findById(1L))
                    .willReturn(Optional.of(product));

            // when & then
            assertThatThrownBy(() ->
                    productService.delete(1L)
            )
                    .isInstanceOf(ProductException.class)
                    .hasMessage("이미 삭제된 상품입니다.");

            assertThat(product.isActive())
                    .isFalse();

            then(productRepository)
                    .should()
                    .findById(1L);
        }
    }

    private Product createProduct(
            Long id,
            String name,
            int price,
            String category,
            int stock,
            boolean isActive
    ) {
        return Product.builder()
                .id(id)
                .name(name)
                .price(price)
                .category(category)
                .filePath("/images/products/" + id + ".jpg")
                .stock(stock)
                .isActive(isActive)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
