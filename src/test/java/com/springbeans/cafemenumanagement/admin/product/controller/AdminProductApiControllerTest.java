package com.springbeans.cafemenumanagement.admin.product.controller;

import com.springbeans.cafemenumanagement.global.config.SecurityConfig;
import com.springbeans.cafemenumanagement.global.constant.RoleConst;
import com.springbeans.cafemenumanagement.product.dto.ProductSaveRequest;
import com.springbeans.cafemenumanagement.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminProductApiController.class)
@ContextConfiguration(
        classes = {
                AdminProductApiController.class,
                SecurityConfig.class
        }
)
class AdminProductApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Nested
    @DisplayName("상품 등록")
    class AddProduct {

        @Test
        @WithMockUser(username = "admin")
        @DisplayName("인증된 관리자 세션으로 상품을 등록하면 201을 반환한다")
        void addProductSuccess() throws Exception {
            // given
            MockMultipartFile image = createImage();

            given(productService.save(any(ProductSaveRequest.class)))
                    .willReturn(
                            ResponseEntity
                                    .status(HttpStatus.CREATED)
                                    .build()
                    );

            // when
            mockMvc.perform(
                            multipart("/api/admin/products")
                                    .file(image)
                                    .param("name", "아메리카노")
                                    .param("price", "4500")
                                    .param("category", "커피")
                                    .param("stock", "10")
                                    .session(createAdminSession())
                                    .with(csrf())
                    )
                    // then
                    .andExpect(status().isCreated());

            ArgumentCaptor<ProductSaveRequest> captor =
                    ArgumentCaptor.forClass(ProductSaveRequest.class);

            then(productService)
                    .should()
                    .save(captor.capture());

            ProductSaveRequest capturedRequest = captor.getValue();

            assertThat(capturedRequest.name())
                    .isEqualTo("아메리카노");

            assertThat(capturedRequest.price())
                    .isEqualTo(4500);

            assertThat(capturedRequest.category())
                    .isEqualTo("커피");

            assertThat(capturedRequest.stock())
                    .isEqualTo(10);

            assertThat(capturedRequest.image())
                    .isNotNull();

            assertThat(capturedRequest.image().getOriginalFilename())
                    .isEqualTo("americano.jpg");
        }

        @Test
        @DisplayName("비로그인 상태에서는 상품 등록 API에 접근할 수 없다")
        void addProductFailWhenUnauthenticated() throws Exception {
            // given
            MockMultipartFile image = createImage();

            // when & then
            mockMvc.perform(
                            multipart("/api/admin/products")
                                    .file(image)
                                    .param("name", "아메리카노")
                                    .param("price", "4500")
                                    .param("category", "커피")
                                    .param("stock", "10")
                                    .with(csrf())
                    )
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/admin/login"));

            then(productService)
                    .should(never())
                    .save(any(ProductSaveRequest.class));
        }

        @Test
        @DisplayName("필수 상품명이 없으면 400을 반환한다")
        void addProductFailWhenNameMissing() throws Exception {
            // given
            MockMultipartFile image = createImage();

            // when & then
            mockMvc.perform(
                            multipart("/api/admin/products")
                                    .file(image)
                                    .param("price", "4500")
                                    .param("category", "커피")
                                    .param("stock", "10")
                                    .session(createAdminSession())
                                    .with(csrf())
                    )
                    .andExpect(status().isBadRequest());

            then(productService)
                    .should(never())
                    .save(any(ProductSaveRequest.class));
        }

        @Test
        @DisplayName("가격 형식이 숫자가 아니면 400을 반환한다")
        void addProductFailWhenPriceInvalid() throws Exception {
            // given
            MockMultipartFile image = createImage();

            // when & then
            mockMvc.perform(
                            multipart("/api/admin/products")
                                    .file(image)
                                    .param("name", "아메리카노")
                                    .param("price", "invalid-price")
                                    .param("category", "커피")
                                    .param("stock", "10")
                                    .session(createAdminSession())
                                    .with(csrf())
                    )
                    .andExpect(status().isBadRequest());

            then(productService)
                    .should(never())
                    .save(any(ProductSaveRequest.class));
        }
    }

    @Nested
    @DisplayName("상품 수정")
    class UpdateProduct {

        @Test
        @DisplayName("인증된 관리자 세션으로 상품을 수정하면 200을 반환한다")
        void updateProductSuccess() throws Exception {
            // given
            MockMultipartFile image = createImage();

            given(
                    productService.update(
                            any(Long.class),
                            any(ProductSaveRequest.class)
                    )
            ).willReturn(ResponseEntity.ok().build());

            // when
            mockMvc.perform(
                            multipart(
                                    "/api/admin/products/{id}",
                                    1L
                            )
                                    .file(image)
                                    .param("name", "카페라떼")
                                    .param("price", "5000")
                                    .param("category", "커피")
                                    .param("stock", "20")
                                    .session(createAdminSession())
                                    .with(csrf())
                                    .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                    })
                    )
                    // then
                    .andExpect(status().isOk());

            ArgumentCaptor<ProductSaveRequest> requestCaptor =
                    ArgumentCaptor.forClass(
                            ProductSaveRequest.class
                    );

            then(productService)
                    .should()
                    .update(
                            org.mockito.ArgumentMatchers.eq(1L),
                            requestCaptor.capture()
                    );

            ProductSaveRequest capturedRequest =
                    requestCaptor.getValue();

            assertThat(capturedRequest.name())
                    .isEqualTo("카페라떼");

            assertThat(capturedRequest.price())
                    .isEqualTo(5000);

            assertThat(capturedRequest.category())
                    .isEqualTo("커피");

            assertThat(capturedRequest.stock())
                    .isEqualTo(20);
        }

        @Test
        @DisplayName("비로그인 상태에서는 상품 수정 API에 접근할 수 없다")
        void updateProductFailWhenUnauthenticated()
                throws Exception {

            // given
            MockMultipartFile image = createImage();

            // when & then
            mockMvc.perform(
                            multipart(
                                    "/api/admin/products/{id}",
                                    1L
                            )
                                    .file(image)
                                    .param("name", "카페라떼")
                                    .param("price", "5000")
                                    .param("category", "커피")
                                    .param("stock", "20")
                                    .with(csrf())
                                    .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                    })
                    )
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/admin/login"));

            then(productService)
                    .should(never())
                    .update(
                            any(Long.class),
                            any(ProductSaveRequest.class)
                    );
        }

        @Test
        @DisplayName("상품 ID가 숫자가 아니면 400을 반환한다")
        void updateProductFailWhenIdInvalid()
                throws Exception {

            // given
            MockMultipartFile image = createImage();

            // when & then
            mockMvc.perform(
                            multipart(
                                    "/api/admin/products/{id}",
                                    "invalid-id"
                            )
                                    .file(image)
                                    .param("name", "카페라떼")
                                    .param("price", "5000")
                                    .param("category", "커피")
                                    .param("stock", "20")
                                    .session(createAdminSession())
                                    .with(csrf())
                                    .with(request -> {
                                        request.setMethod("PUT");
                                        return request;
                                    })
                    )
                    .andExpect(status().isBadRequest());

            then(productService)
                    .shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("상품 삭제")
    class DeleteProduct {

        @Test
        @DisplayName("인증된 관리자 세션으로 상품을 삭제하면 204를 반환한다")
        void deleteProductSuccess() throws Exception {
            // given
            given(productService.delete(1L))
                    .willReturn(
                            ResponseEntity
                                    .noContent()
                                    .build()
                    );

            // when & then
            mockMvc.perform(
                            delete(
                                    "/api/admin/products/{id}",
                                    1L
                            )
                                    .session(createAdminSession())
                                    .with(csrf())
                    )
                    .andExpect(status().isNoContent());

            then(productService)
                    .should()
                    .delete(1L);
        }

        @Test
        @DisplayName("비로그인 상태에서는 상품 삭제 API에 접근할 수 없다")
        void deleteProductFailWhenUnauthenticated()
                throws Exception {

            // when & then
            mockMvc.perform(
                            delete(
                                    "/api/admin/products/{id}",
                                    1L
                            )
                                    .with(csrf())
                    )
                    .andExpect(status().isFound())
                    .andExpect(redirectedUrl("/admin/login"));

            then(productService)
                    .should(never())
                    .delete(any(Long.class));
        }

        @Test
        @DisplayName("상품 ID가 숫자가 아니면 400을 반환한다")
        void deleteProductFailWhenIdInvalid()
                throws Exception {

            // when & then
            mockMvc.perform(
                            delete(
                                    "/api/admin/products/{id}",
                                    "invalid-id"
                            )
                                    .session(createAdminSession())
                                    .with(csrf())
                    )
                    .andExpect(status().isBadRequest());

            then(productService)
                    .shouldHaveNoInteractions();
        }
    }

    private MockMultipartFile createImage() {
        return new MockMultipartFile(
                "image",
                "americano.jpg",
                "image/jpeg",
                "test-image-data".getBytes()
        );
    }

    private MockHttpSession createAdminSession() {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        "admin",
                        null,
                        List.of(
                                new SimpleGrantedAuthority(
                                        RoleConst.ADMIN
                                )
                        )
                );

        SecurityContext securityContext =
                new SecurityContextImpl();

        securityContext.setAuthentication(authentication);

        MockHttpSession session = new MockHttpSession();

        session.setAttribute(
                HttpSessionSecurityContextRepository
                        .SPRING_SECURITY_CONTEXT_KEY,
                securityContext
        );

        return session;
    }
}
