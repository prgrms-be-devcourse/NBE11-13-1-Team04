package com.springbeans.cafemenumanagement.product.controller;

import com.springbeans.cafemenumanagement.product.dto.ProductResponse;
import com.springbeans.cafemenumanagement.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductApiController.class)
class ProductApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Nested
    @DisplayName("상품 단건 조회")
    class GetProduct {

        @Test
        @DisplayName("상품 ID로 조회하면 200과 상품 정보를 반환한다")
        void getProductSuccess() throws Exception {
            // given
            ProductResponse product = createProductResponse(
                    1L,
                    "아메리카노",
                    4500,
                    "커피",
                    10
            );

            given(productService.get(1L))
                    .willReturn(ResponseEntity.ok(product));

            // when & then
            mockMvc.perform(
                            get("/product/{id}", 1L)
                                    .with(anonymous())
                    )
                    .andExpect(status().isOk())
                    .andExpect(
                            content().contentTypeCompatibleWith(
                                    "application/json"
                            )
                    )
                    .andExpect(jsonPath("$.id").value(1L))
                    .andExpect(
                            jsonPath("$.name")
                                    .value("아메리카노")
                    )
                    .andExpect(
                            jsonPath("$.price")
                                    .value(4500)
                    )
                    .andExpect(
                            jsonPath("$.category")
                                    .value("커피")
                    )
                    .andExpect(
                            jsonPath("$.filePath")
                                    .value(
                                            "/images/products/1.jpg"
                                    )
                    )
                    .andExpect(
                            jsonPath("$.stock")
                                    .value(10)
                    );

            then(productService)
                    .should()
                    .get(1L);
        }

        @Test
        @DisplayName("숫자가 아닌 상품 ID를 요청하면 400을 반환한다")
        void getProductFailWhenIdInvalid() throws Exception {
            // when & then
            mockMvc.perform(
                            get("/product/{id}", "invalid-id")
                                    .with(anonymous())
                    )
                    .andExpect(status().isBadRequest());

            verifyNoInteractions(productService);
        }
    }

    @Nested
    @DisplayName("전체 상품 조회")
    class GetAllProducts {

        @Test
        @DisplayName("전체 상품을 조회하면 200과 상품 배열을 반환한다")
        void getAllProductsSuccess() throws Exception {
            // given
            ProductResponse firstProduct =
                    createProductResponse(
                            1L,
                            "아메리카노",
                            4500,
                            "커피",
                            10
                    );

            ProductResponse secondProduct =
                    createProductResponse(
                            2L,
                            "카페라떼",
                            5000,
                            "커피",
                            5
                    );

            given(productService.getAll())
                    .willReturn(
                            ResponseEntity.ok(
                                    List.of(
                                            firstProduct,
                                            secondProduct
                                    )
                            )
                    );

            // when & then
            mockMvc.perform(
                            get("/product")
                                    .with(anonymous())
                    )
                    .andExpect(status().isOk())
                    .andExpect(
                            content().contentTypeCompatibleWith(
                                    "application/json"
                            )
                    )
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(
                            jsonPath("$.length()")
                                    .value(2)
                    )
                    .andExpect(
                            jsonPath("$[0].id")
                                    .value(1L)
                    )
                    .andExpect(
                            jsonPath("$[0].name")
                                    .value("아메리카노")
                    )
                    .andExpect(
                            jsonPath("$[0].stock")
                                    .value(10)
                    )
                    .andExpect(
                            jsonPath("$[1].id")
                                    .value(2L)
                    )
                    .andExpect(
                            jsonPath("$[1].name")
                                    .value("카페라떼")
                    )
                    .andExpect(
                            jsonPath("$[1].stock")
                                    .value(5)
                    );

            then(productService)
                    .should()
                    .getAll();
        }

        @Test
        @DisplayName("조회할 상품이 없으면 200과 빈 배열을 반환한다")
        void getAllProductsEmpty() throws Exception {
            // given
            given(productService.getAll())
                    .willReturn(
                            ResponseEntity.ok(List.of())
                    );

            // when & then
            mockMvc.perform(
                            get("/product")
                                    .with(anonymous())
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());

            then(productService)
                    .should()
                    .getAll();
        }
    }

    @Nested
    @DisplayName("카테고리별 상품 조회")
    class GetProductsByCategory {

        @Test
        @DisplayName("카테고리로 조회하면 200과 해당 상품 목록을 반환한다")
        void getProductsByCategorySuccess() throws Exception {
            // given
            ProductResponse firstProduct =
                    createProductResponse(
                            1L,
                            "아메리카노",
                            4500,
                            "커피",
                            10
                    );

            ProductResponse secondProduct =
                    createProductResponse(
                            2L,
                            "카페라떼",
                            5000,
                            "커피",
                            5
                    );

            given(productService.getByCategory("커피"))
                    .willReturn(
                            ResponseEntity.ok(
                                    List.of(
                                            firstProduct,
                                            secondProduct
                                    )
                            )
                    );

            // when & then
            mockMvc.perform(
                            get(
                                    "/product/category/{category}",
                                    "커피"
                            )
                                    .with(anonymous())
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(
                            jsonPath("$.length()")
                                    .value(2)
                    )
                    .andExpect(
                            jsonPath("$[0].category")
                                    .value("커피")
                    )
                    .andExpect(
                            jsonPath("$[1].category")
                                    .value("커피")
                    );

            then(productService)
                    .should()
                    .getByCategory("커피");
        }

        @Test
        @DisplayName("카테고리에 해당하는 상품이 없으면 빈 배열을 반환한다")
        void getProductsByCategoryEmpty() throws Exception {
            // given
            given(productService.getByCategory("디저트"))
                    .willReturn(
                            ResponseEntity.ok(List.of())
                    );

            // when & then
            mockMvc.perform(
                            get(
                                    "/product/category/{category}",
                                    "디저트"
                            )
                                    .with(anonymous())
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$").isEmpty());

            then(productService)
                    .should()
                    .getByCategory("디저트");
        }
    }

    private ProductResponse createProductResponse(
            Long id,
            String name,
            int price,
            String category,
            int stock
    ) {
        return new ProductResponse(
                id,
                name,
                price,
                category,
                "/images/products/" + id + ".jpg",
                stock
        );
    }
}