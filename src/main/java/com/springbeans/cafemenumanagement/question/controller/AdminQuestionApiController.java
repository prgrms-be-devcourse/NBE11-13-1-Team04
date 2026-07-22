package com.springbeans.cafemenumanagement.question.controller;

import com.springbeans.cafemenumanagement.question.dto.AdminQuestionDetailResponse;
import com.springbeans.cafemenumanagement.question.dto.AdminQuestionPreviewResponse;
import com.springbeans.cafemenumanagement.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/questions")
@RequiredArgsConstructor
public class AdminQuestionApiController {

    private final QuestionService questionService;

    // 관리자 전체 문의 목록 조회
    @GetMapping
    public ResponseEntity<List<AdminQuestionPreviewResponse>> getQuestions() {

        List<AdminQuestionPreviewResponse> response = questionService.getAdminQuestions();

        return ResponseEntity.ok(response);
    }

    // 관리자 문의 상세 조회
    @GetMapping("/{questionId}")
    public ResponseEntity<AdminQuestionDetailResponse> getQuestion(
            @PathVariable Long questionId
    ) {
        AdminQuestionDetailResponse response = questionService.getAdminQuestion(questionId);

        return ResponseEntity.ok(response);
    }
}