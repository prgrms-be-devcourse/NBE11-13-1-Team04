package com.springbeans.cafemenumanagement.question.service;

import com.springbeans.cafemenumanagement.answer.domain.repository.AnswerRepository;
import com.springbeans.cafemenumanagement.answer.dto.response.AnswerDetailResponse;
import com.springbeans.cafemenumanagement.global.exception.BusinessException;
import com.springbeans.cafemenumanagement.global.slack.SlackNotificationService;
import com.springbeans.cafemenumanagement.question.domain.entity.Question;
import com.springbeans.cafemenumanagement.question.domain.repository.QuestionRepository;
import com.springbeans.cafemenumanagement.question.dto.request.QuestionAuthRequest;
import com.springbeans.cafemenumanagement.question.dto.request.QuestionCreateRequest;
import com.springbeans.cafemenumanagement.question.dto.response.*;
import com.springbeans.cafemenumanagement.question.exception.QuestionErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final SlackNotificationService slackNotificationService;

    @Transactional
    public QuestionSaveResponse create(QuestionCreateRequest request) {

        validateCreateRequest(request);

        Question question = Question.create(
                request.email(),
                request.title(),
                request.content()
        );

        questionRepository.save(question);

        slackNotificationService.sendInquiryNotification(
                question.getEmail(),
                question.getTitle(),
                question.getContent()
        );

        return QuestionSaveResponse.from(question);
    }

    public List<QuestionPreviewResponse> getQuestions(String email) {
        return questionRepository
                .findByEmailAndIsActiveTrueOrderByCreatedAtDesc(email)
                .stream()
                .map(QuestionPreviewResponse::from)
                .toList();
    }

    public QuestionDetailResponse getQuestion(Long id, String email) {

        if (email == null || email.isBlank()) {
            throw new BusinessException(
                    QuestionErrorCode.QUESTION_EMAIL_REQUIRED
            );
        }

        Question question = questionRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new BusinessException(
                        QuestionErrorCode.QUESTION_NOT_FOUND,
                        "Question not found. questionId=" + id
                ));

        if (!question.getEmail().equals(email)) {
            throw new BusinessException(
                    QuestionErrorCode.QUESTION_ACCESS_DENIED,
                    "Question access denied. questionId=" + id
                            + ", requestEmail=" + email
            );
        }

        AnswerDetailResponse answer =
                answerRepository.findByQuestionIdAndIsActiveTrue(id)
                        .map(AnswerDetailResponse::from)
                        .orElse(null);

        return QuestionDetailResponse.from(question, answer);
    }

    public List<AdminQuestionPreviewResponse> getAdminQuestions() {
        return questionRepository
                .findAllByIsActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(AdminQuestionPreviewResponse::from)
                .toList();
    }

    public AdminQuestionDetailResponse getAdminQuestion(Long id) {

        Question question = questionRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new BusinessException(
                        QuestionErrorCode.QUESTION_NOT_FOUND,
                        "Question not found. questionId=" + id
                ));

        AnswerDetailResponse answer =
                answerRepository.findByQuestionIdAndIsActiveTrue(id)
                        .map(AnswerDetailResponse::from)
                        .orElse(null);

        return AdminQuestionDetailResponse.from(question, answer);
    }

    @Transactional
    public void delete(
            Long questionId,
            QuestionAuthRequest request
    ) {

        if (request == null) {
            throw new BusinessException(
                    QuestionErrorCode.QUESTION_AUTH_REQUEST_REQUIRED
            );
        }

        if (request.email() == null || request.email().isBlank()) {
            throw new BusinessException(
                    QuestionErrorCode.QUESTION_EMAIL_REQUIRED
            );
        }

        Question question =
                questionRepository.findByIdAndIsActiveTrue(questionId)
                        .orElseThrow(() -> new BusinessException(
                                QuestionErrorCode.QUESTION_NOT_FOUND,
                                "Question not found. questionId=" + questionId
                        ));

        if (!question.getEmail().equals(request.email())) {
            throw new BusinessException(
                    QuestionErrorCode.QUESTION_DELETE_ACCESS_DENIED,
                    "Question delete access denied. questionId="
                            + questionId
                            + ", requestEmail=" + request.email()
            );
        }

        answerRepository.findByQuestionIdAndIsActiveTrue(questionId)
                .ifPresent(answer -> answer.deactivate());

        question.deactivate();
    }

    private void validateCreateRequest(
            QuestionCreateRequest request
    ) {

        if (request == null) {
            throw new BusinessException(
                    QuestionErrorCode.QUESTION_REQUEST_REQUIRED
            );
        }

        if (request.email() == null || request.email().isBlank()) {
            throw new BusinessException(
                    QuestionErrorCode.QUESTION_EMAIL_REQUIRED
            );
        }

        if (request.title() == null || request.title().isBlank()) {
            throw new BusinessException(
                    QuestionErrorCode.QUESTION_TITLE_REQUIRED
            );
        }

        if (request.content() == null || request.content().isBlank()) {
            throw new BusinessException(
                    QuestionErrorCode.QUESTION_CONTENT_REQUIRED
            );
        }
    }
}