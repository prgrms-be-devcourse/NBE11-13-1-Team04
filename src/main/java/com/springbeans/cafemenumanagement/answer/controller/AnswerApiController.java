package com.springbeans.cafemenumanagement.answer.controller;

import com.springbeans.cafemenumanagement.answer.dto.AnswerSaveRequest;
import com.springbeans.cafemenumanagement.answer.dto.AnswerSaveResponse;
import com.springbeans.cafemenumanagement.answer.service.AnswerService;
import com.springbeans.cafemenumanagement.global.constant.SessionConst;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/questions")
@RequiredArgsConstructor
public class AnswerApiController {

    private final AnswerService answerService;

    // 관리자 답변 등록
    @PostMapping("/{questionId}/answers")
    public ResponseEntity<AnswerSaveResponse> create(
            @PathVariable Long questionId,
            @RequestBody AnswerSaveRequest request,
            HttpServletRequest httpRequest
    ) {
        // 현재 세션 조회
        HttpSession session = httpRequest.getSession(false);

        if (session == null) {
            // TODO : 관리자 인증 관련 커스텀 예외 적용
            throw new IllegalArgumentException("관리자 로그인이 필요합니다.");
        }

        // 로그인한 관리자 ID 조회
        Long adminId = (Long) session.getAttribute(SessionConst.ADMIN_ID);

        if (adminId == null) {
            // TODO : 관리자 인증 관련 커스텀 예외 적용
            throw new IllegalArgumentException("관리자 로그인이 필요합니다.");
        }

        // 답변 등록
        AnswerSaveResponse response = answerService.create(questionId, adminId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}