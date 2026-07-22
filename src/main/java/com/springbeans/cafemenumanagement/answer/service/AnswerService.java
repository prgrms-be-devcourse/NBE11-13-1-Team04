package com.springbeans.cafemenumanagement.answer.service;

import com.springbeans.cafemenumanagement.admin.auth.domain.entity.Admin;
import com.springbeans.cafemenumanagement.admin.auth.domain.repository.AdminRepository;
import com.springbeans.cafemenumanagement.answer.domain.entity.Answer;
import com.springbeans.cafemenumanagement.answer.domain.repository.AnswerRepository;
import com.springbeans.cafemenumanagement.answer.dto.AnswerSaveRequest;
import com.springbeans.cafemenumanagement.answer.dto.AnswerSaveResponse;
import com.springbeans.cafemenumanagement.question.domain.entity.Question;
import com.springbeans.cafemenumanagement.question.domain.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AdminRepository adminRepository;

    @Transactional
    public AnswerSaveResponse create(
            Long questionId,
            Long adminId,
            AnswerSaveRequest request
    ) {

        // 답변 등록 요청값 검증
        validateCreateRequest(request);

        // 답변을 등록할 문의 조회
        Question question = questionRepository.findById(questionId)
                // TODO : 문의 관련 커스텀 예외 적용
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 문의입니다.")
                );

        // 한 문의에는 답변을 하나만 등록할 수 있도록 중복 확인
        if (answerRepository.existsByQuestionId(questionId)) {
            // TODO : 답변 관련 커스텀 예외 적용
            throw new IllegalArgumentException("이미 답변이 등록된 문의입니다.");
        }

        // 세션에 저장된 관리자 ID로 로그인한 관리자 조회
        Admin admin = adminRepository.findById(adminId)
                // TODO : 관리자 관련 커스텀 예외 적용
                .orElseThrow(() ->
                        new IllegalArgumentException("존재하지 않는 관리자입니다.")
                );

        // 문의, 답변 내용, 관리자를 연결하여 답변 생성
        Answer answer = Answer.create(
                question,
                request.content(),
                admin
        );

        // 답변 저장
        answerRepository.save(answer);

        // 문의 상태를 답변 완료로 변경
        question.markAsAnswered();

        // 저장된 답변 정보 반환
        return AnswerSaveResponse.from(answer);
    }

    private void validateCreateRequest(AnswerSaveRequest request) {

        // TODO : 답변 관련 커스텀 예외 적용
        if (request == null
                || request.content() == null
                || request.content().isBlank()) {
            throw new IllegalArgumentException("답변 내용은 필수입니다.");
        }
    }
}