package com.springbeans.cafemenumanagement.question.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/questions")
public class QuestionViewController {

    // 문의 등록 페이지
    @GetMapping("/new")
    public String createForm() {
        return "question-create";
    }

    // 문의 목록 조회 페이지
    @GetMapping
    public String listForm() {
        return "question-list";
    }

    // 문의 상세 조회 페이지
    @GetMapping("/{questionId}")
    public String detailForm(
            @PathVariable Long questionId,
            @RequestParam String email,
            Model model
    ) {
        model.addAttribute("questionId", questionId);
        model.addAttribute("email", email);

        return "question-detail";
    }
}