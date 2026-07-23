package com.springbeans.cafemenumanagement.admin.auth.service;

import com.springbeans.cafemenumanagement.admin.auth.domain.entity.Admin;
import com.springbeans.cafemenumanagement.admin.auth.domain.repository.AdminRepository;
import com.springbeans.cafemenumanagement.admin.auth.dto.LoginRequest;
import com.springbeans.cafemenumanagement.admin.auth.dto.LoginResponse;
import com.springbeans.cafemenumanagement.global.constant.RoleConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Builder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletRequest httpRequest;

    @Mock
    private HttpSession session;

    @AfterEach
    void tearDown() {
        // 테스트 간 SecurityContext 상태 오염 방지
        SecurityContextHolder.clearContext();
    }

    @Nested
    @DisplayName("로그인 테스트")
    class LoginTest {

        @Test
        @DisplayName("성공: 올바른 아이디와 비밀번호 입력 시 로그인에 성공하고 세션 및 SecurityContext에 인증 정보가 저장된다")
        void login_Success() {
            // given
            LoginRequest request = new LoginRequest("adminUser", "rawPassword123!");
            Admin mockAdmin = Admin.builder()
                    .id(1L)
                    .username("adminUser")
                    .password("encodedPassword123!")
                    .build();

            given(adminRepository.findByUsername(request.username()))
                    .willReturn(Optional.of(mockAdmin));
            given(passwordEncoder.matches(request.password(), mockAdmin.getPassword()))
                    .willReturn(true);
            given(httpRequest.getSession(true))
                    .willReturn(session);

            // when
            LoginResponse response = authService.login(request, httpRequest);

            // then
            assertThat(response).isNotNull();
            assertThat(response.adminId()).isEqualTo(1L);
            assertThat(response.username()).isEqualTo("adminUser");

            // SecurityContext 검증
            var authentication = SecurityContextHolder.getContext().getAuthentication();
            assertThat(authentication).isNotNull();
            assertThat(authentication.getName()).isEqualTo("adminUser");
            assertThat(authentication.getAuthorities())
                    .extracting("authority")
                    .containsExactly(RoleConst.ADMIN);

            // Session 저장 검증
            verify(session).setAttribute(
                    eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY),
                    any()
                                        );
        }

        @Test
        @DisplayName("실패: 존재하지 않는 아이디 입력 시 예외가 발생한다")
        void login_Fail_UserNotFound() {
            // given
            LoginRequest request = new LoginRequest("nonExistUser", "password");

            given(adminRepository.findByUsername(request.username()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authService.login(request, httpRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("아이디 또는 비밀번호가 일치하지 않습니다.");

            verify(passwordEncoder, never()).matches(anyString(), anyString());
            verify(httpRequest, never()).getSession(anyBoolean());
        }

        @Test
        @DisplayName("실패: 비밀번호 불일치 시 예외가 발생한다")
        void login_Fail_InvalidPassword() {
            // given
            LoginRequest request = new LoginRequest("adminUser", "wrongPassword");
            Admin mockAdmin = Admin.builder()
                    .id(1L)
                    .username("adminUser")
                    .password("encodedPassword123!")
                    .build();

            given(adminRepository.findByUsername(request.username()))
                    .willReturn(Optional.of(mockAdmin));
            given(passwordEncoder.matches(request.password(), mockAdmin.getPassword()))
                    .willReturn(false);

            // when & then
            assertThatThrownBy(() -> authService.login(request, httpRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("아이디 또는 비밀번호가 일치하지 않습니다.");

            verify(httpRequest, never()).getSession(anyBoolean());
        }
    }

    @Nested
    @DisplayName("로그아웃 테스트")
    class LogoutTest {

        @Test
        @DisplayName("성공: 로그아웃 시 SecurityContext가 클리어되고 기존 세션이 존재하면 무효화(invalidate)된다")
        void logout_Success_WithExistingSession() {
            // given
            given(httpRequest.getSession(false)).willReturn(session);

            // when
            authService.logout(httpRequest);

            // then
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(session, times(1)).invalidate();
        }

        @Test
        @DisplayName("성공: 기존 세션이 존재하지 않는 상태에서도 에러 없이 SecurityContext가 클리어된다")
        void logout_Success_WhenSessionIsNull() {
            // given
            given(httpRequest.getSession(false)).willReturn(null);

            // when
            authService.logout(httpRequest);

            // then
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
            verify(session, never()).invalidate();
        }
    }
}