package com.springbeans.cafemenumanagement.question.controller;

import com.springbeans.cafemenumanagement.question.dto.request.QuestionAuthRequest;
import com.springbeans.cafemenumanagement.question.dto.request.QuestionCreateRequest;
import com.springbeans.cafemenumanagement.question.dto.response.QuestionDetailResponse;
import com.springbeans.cafemenumanagement.question.dto.response.QuestionPreviewResponse;
import com.springbeans.cafemenumanagement.question.dto.response.QuestionSaveResponse;
import com.springbeans.cafemenumanagement.question.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
public class QuestionApiController {

    private final QuestionService questionService;

    // 문의 등록
    @PostMapping
    public ResponseEntity<QuestionSaveResponse> create(
            @RequestBody QuestionCreateRequest request
                                                      ) {
        QuestionSaveResponse response = questionService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // 이메일로 본인 문의 목록 조회
    @GetMapping
    public ResponseEntity<List<QuestionPreviewResponse>> getQuestions(
            @RequestParam String email
                                                                     ) {
        List<QuestionPreviewResponse> response = questionService.getQuestions(email);

        return ResponseEntity.ok(response);
    }

    // 본인 문의 상세 조회
    @GetMapping("/{questionId}")
    public ResponseEntity<QuestionDetailResponse> getQuestion(
            @PathVariable Long questionId,
            @RequestParam String email
                                                             ) {
        QuestionDetailResponse response = questionService.getQuestion(questionId, email);

        return ResponseEntity.ok(response);
    }

    // 본인 문의 삭제
    @DeleteMapping("/{questionId}")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long questionId,
            @RequestBody QuestionAuthRequest request
    ) {
        questionService.delete(questionId, request);

        return ResponseEntity.noContent().build();
    }
}