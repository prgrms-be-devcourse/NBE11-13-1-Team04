package com.springbeans.cafemenumanagement.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain( HttpSecurity http ) throws Exception {

        http
                // API 로그인(JSON)을 직접 처리하므로 CSRF는 비활성화
                .csrf(csrf -> csrf.disable())

                //  Spring Security 자체 폼 로그인은 해제 (직접 Controller를 짰기 때문)
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                // 인가(접근 권한) 설정 규칙
                .authorizeHttpRequests(auth -> auth
                                               // 로그인 화면, 로그인 API, 그리고 static 리소스는 누구나 접근 가능
                                               // 접근 가능 화면 여기에 추가
                                               .requestMatchers("/admin/login", "/admin/auth/login", "/css/**", "/js/**").permitAll()

                                               // 시큐리티 세션 인증(authenticated)이 완료된 상태여야만 접근 가능
                                               .requestMatchers("/admin/**", "/api/admin/**").authenticated()

                                               // 나머지 모든 요청도 우선 허용
                                               .anyRequest().permitAll()
                                      )

                // 세션이 없거나 쿠키를 지운 익명 사용자가 /admin/**, 또는 /api/admin/** 주소로 강제 진입 시도할 때 작동
                .exceptionHandling(exception -> exception
                                           .authenticationEntryPoint(( request, response, authException ) -> {
                                               // 로그인로 페이지 리다이렉트
                                               response.sendRedirect("/admin/login");
                                           })
                                  );

        return http.build();
    }
}