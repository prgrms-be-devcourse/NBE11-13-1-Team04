package com.springbeans.cafemenumanagement.admin.auth.controller;

import com.springbeans.cafemenumanagement.admin.auth.dto.LoginRequest;
import com.springbeans.cafemenumanagement.admin.auth.dto.LoginResponse;
import com.springbeans.cafemenumanagement.admin.auth.service.AuthService;
import com.springbeans.cafemenumanagement.global.constant.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/auth")
@RequiredArgsConstructor
public class AuthApiController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody @Valid LoginRequest request,
            HttpServletRequest httpRequest
                                              ) {
        LoginResponse response = authService.login(request, httpRequest);

        HttpSession session = httpRequest.getSession();
        session.setAttribute(SessionConst.ADMIN_ID, response.adminId());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logout( HttpServletRequest request ) {
        authService.logout(request);
        return ResponseEntity.ok().build();
    }
}