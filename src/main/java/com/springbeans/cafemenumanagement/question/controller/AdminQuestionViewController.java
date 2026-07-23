package com.springbeans.cafemenumanagement.question.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminQuestionViewController {

    // 관리자 문의 목록 화면
    @GetMapping("/questions")
    public String listForm() {
        return "admin/admin-question-list";
    }

    // 관리자 문의 상세 및 답변 작성 화면
    @GetMapping("/questions/{questionId}")
    public String detailForm(
            @PathVariable Long questionId,
            Model model
    ) {
        // 화면에서 상세 조회와 답변 등록에 사용할 문의 ID 전달
        model.addAttribute("questionId", questionId);

        return "admin/admin-question-detail";
    }
}