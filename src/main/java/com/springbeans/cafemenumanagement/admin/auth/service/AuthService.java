package com.springbeans.cafemenumanagement.admin.auth.service;

import com.springbeans.cafemenumanagement.admin.auth.domain.entity.Admin;
import com.springbeans.cafemenumanagement.admin.auth.domain.repository.AdminRepository;
import com.springbeans.cafemenumanagement.admin.auth.dto.LoginRequest;
import com.springbeans.cafemenumanagement.admin.auth.dto.LoginResponse;
import com.springbeans.cafemenumanagement.admin.auth.exception.AuthErrorCode;
import com.springbeans.cafemenumanagement.global.constant.RoleConst;
import com.springbeans.cafemenumanagement.global.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        Admin admin = adminRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(AuthErrorCode.INVALID_CREDENTIALS));;

        if (!passwordEncoder.matches(request.password(), admin.getPassword())) {
            throw new BusinessException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        // 시큐리티 전용 인증 토큰 생성
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                admin.getUsername(),
                null,
                List.of(new SimpleGrantedAuthority(RoleConst.ADMIN))
        );

        // 현재 스레드의 SecurityContext에 인증 정보 설정
        SecurityContextHolder.getContext().setAuthentication(token);

        // 생성된 시큐리티 인증 정보를 현재 HTTP 세션에 동기화 저장
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                SecurityContextHolder.getContext()
                            );

        return new LoginResponse(
                admin.getId(),
                admin.getUsername()
        );
    }

    public void logout(HttpServletRequest httpRequest) {
        // 현재 스레드의 인증 정보부터 먼저 클리어
        SecurityContextHolder.clearContext();

        // 세션 무효화
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}