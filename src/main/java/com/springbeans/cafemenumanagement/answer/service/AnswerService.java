package com.springbeans.cafemenumanagement.answer.service;

import com.springbeans.cafemenumanagement.admin.auth.domain.entity.Admin;
import com.springbeans.cafemenumanagement.admin.auth.domain.repository.AdminRepository;
import com.springbeans.cafemenumanagement.admin.auth.exception.AuthErrorCode;
import com.springbeans.cafemenumanagement.answer.domain.entity.Answer;
import com.springbeans.cafemenumanagement.answer.domain.repository.AnswerRepository;
import com.springbeans.cafemenumanagement.answer.dto.request.AnswerSaveRequest;
import com.springbeans.cafemenumanagement.answer.dto.response.AnswerSaveResponse;
import com.springbeans.cafemenumanagement.answer.event.AnswerCreatedEvent;
import com.springbeans.cafemenumanagement.answer.exception.AnswerErrorCode;
import com.springbeans.cafemenumanagement.global.exception.BusinessException;
import com.springbeans.cafemenumanagement.question.domain.entity.Question;
import com.springbeans.cafemenumanagement.question.domain.repository.QuestionRepository;
import com.springbeans.cafemenumanagement.question.exception.QuestionErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AdminRepository adminRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AnswerSaveResponse create(
            Long questionId,
            Long adminId,
            AnswerSaveRequest request
                                    ) {
        // 답변 등록 요청값 검증
        validateCreateRequest(request);

        // 활성 문의 조회
        Question question = questionRepository.findByIdAndIsActiveTrue(questionId)
                .orElseThrow(() -> new BusinessException(QuestionErrorCode.QUESTION_NOT_FOUND));

        // 해당 문의에 활성 답변 데이터가 이미 있는지 확인
        if (answerRepository.existsByQuestionIdAndIsActiveTrue(questionId)) {
            throw new BusinessException(AnswerErrorCode.ANSWER_ALREADY_EXISTS);
        }

        // 세션에 저장된 관리자 ID로 로그인한 관리자 조회
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(AuthErrorCode.UNAUTHORIZED));

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

        eventPublisher.publishEvent(
                new AnswerCreatedEvent(
                        question.getEmail(),
                        question.getTitle(),
                        answer.getContent()
                )
                                   );

        // 저장된 답변 정보 반환
        return AnswerSaveResponse.from(answer);
    }

    @Transactional
    public void delete(Long answerId) {

        // 답변 id로 활성 답변 조회
        Answer answer = answerRepository.findByIdAndIsActiveTrue(answerId)
                .orElseThrow(() -> new BusinessException(AnswerErrorCode.ANSWER_NOT_FOUND));

        // 답변 비활성화
        answer.deactivate();

        // 연결된 문의 상태를 답변 대기로 변경
        answer.getQuestion().markAsWaiting();
    }

    private void validateCreateRequest(AnswerSaveRequest request) {
        if (request == null
            || request.content() == null
            || request.content().isBlank()) {
            throw new BusinessException(AnswerErrorCode.EMPTY_CONTENT);
        }
    }
}