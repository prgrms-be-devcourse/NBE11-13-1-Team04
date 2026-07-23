package com.springbeans.cafemenumanagement.answer.controller;

import com.springbeans.cafemenumanagement.admin.auth.exception.AuthErrorCode;
import com.springbeans.cafemenumanagement.answer.dto.request.AnswerSaveRequest;
import com.springbeans.cafemenumanagement.answer.dto.response.AnswerSaveResponse;
import com.springbeans.cafemenumanagement.answer.service.AnswerService;
import com.springbeans.cafemenumanagement.global.constant.SessionConst;
import com.springbeans.cafemenumanagement.global.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AnswerApiController {

    private final AnswerService answerService;

    // 관리자 답변 등록
    @PostMapping("/questions/{questionId}/answers")
    public ResponseEntity<AnswerSaveResponse> create(
            @PathVariable Long questionId,
            @Valid @RequestBody AnswerSaveRequest request, // 1. DTO 유효성 검증 추가
            HttpServletRequest httpRequest
                                                    ) {
        // 2. 세션 및 관리자 PK 검증 (TODO 해결)
        Long adminId = extractAdminId(httpRequest);

        // 답변 등록
        AnswerSaveResponse response = answerService.create(questionId, adminId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 관리자 답변 삭제
    @DeleteMapping("/answers/{answerId}")
    public ResponseEntity<Void> deleteAnswer(
            @PathVariable Long answerId
                                            ) {
        answerService.delete(answerId);

        return ResponseEntity.noContent().build();
    }

    // 세션에서 관리자 PK 추출 및 검증 Helper 메서드
    private Long extractAdminId(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);

        if (session == null) {
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
        }

        Long adminId = (Long) session.getAttribute(SessionConst.ADMIN_ID);

        if (adminId == null) {
            throw new BusinessException(AuthErrorCode.UNAUTHORIZED);
        }

        return adminId;
    }
}