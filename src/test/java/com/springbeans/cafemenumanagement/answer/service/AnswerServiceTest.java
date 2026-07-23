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
import com.springbeans.cafemenumanagement.question.domain.entity.QuestionStatus;
import com.springbeans.cafemenumanagement.question.domain.repository.QuestionRepository;
import com.springbeans.cafemenumanagement.question.exception.QuestionErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private AnswerService answerService;

    @BeforeEach
    void setUp() {
        answerService = new AnswerService(
                answerRepository,
                questionRepository,
                adminRepository,
                eventPublisher
        );
    }

    @Nested
    @DisplayName("답변 등록")
    class CreateAnswer {

        @Test
        @DisplayName("유효한 요청이면 답변을 등록하고 문의 상태를 ANSWERED로 변경한다")
        void createSuccess() {
            // given
            Question question = Question.create(
                    "user@example.com",
                    "문의 제목",
                    "문의 내용"
            );

            Admin admin = new Admin(
                    "admin",
                    "password"
            );

            AnswerSaveRequest request =
                    new AnswerSaveRequest(
                            "답변 내용입니다."
                    );

            given(questionRepository.findByIdAndIsActiveTrue(1L))
                    .willReturn(Optional.of(question));

            given(
                    answerRepository
                            .existsByQuestionIdAndIsActiveTrue(1L)
            )
                    .willReturn(false);

            given(adminRepository.findById(10L))
                    .willReturn(Optional.of(admin));

            given(answerRepository.save(any(Answer.class)))
                    .willAnswer(invocation ->
                            invocation.getArgument(0)
                    );

            // when
            AnswerSaveResponse response =
                    answerService.create(
                            1L,
                            10L,
                            request
                    );

            // then
            ArgumentCaptor<Answer> answerCaptor =
                    ArgumentCaptor.forClass(Answer.class);

            then(answerRepository)
                    .should()
                    .save(answerCaptor.capture());

            Answer savedAnswer = answerCaptor.getValue();

            assertThat(savedAnswer.getQuestion())
                    .isSameAs(question);

            assertThat(savedAnswer.getAdmin())
                    .isSameAs(admin);

            assertThat(savedAnswer.getContent())
                    .isEqualTo("답변 내용입니다.");

            assertThat(savedAnswer.isActive())
                    .isTrue();

            assertThat(savedAnswer.getCreatedAt())
                    .isNotNull();

            assertThat(question.getStatus())
                    .isEqualTo(QuestionStatus.ANSWERED);

            assertThat(response)
                    .isNotNull();

            then(eventPublisher)
                    .should()
                    .publishEvent(
                            any(AnswerCreatedEvent.class)
                    );
        }

        @Test
        @DisplayName("답변 등록 요청이 null이면 등록에 실패한다")
        void createFailWhenRequestIsNull() {
            // when & then
            assertThatThrownBy(() ->
                    answerService.create(
                            1L,
                            10L,
                            null
                    )
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(AnswerErrorCode.ANSWER_REQUEST_REQUIRED)
                    );

            then(questionRepository)
                    .shouldHaveNoInteractions();

            then(answerRepository)
                    .should(never())
                    .save(any(Answer.class));

            then(eventPublisher)
                    .shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("답변 내용이 공백이면 등록에 실패한다")
        void createFailWhenContentIsBlank() {
            // given
            AnswerSaveRequest request =
                    new AnswerSaveRequest(" ");

            // when & then
            assertThatThrownBy(() ->
                    answerService.create(
                            1L,
                            10L,
                            request
                    )
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(AnswerErrorCode.ANSWER_CONTENT_REQUIRED)
                    );

            then(questionRepository)
                    .shouldHaveNoInteractions();

            then(answerRepository)
                    .should(never())
                    .save(any(Answer.class));

            then(eventPublisher)
                    .shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("활성 문의가 존재하지 않으면 답변 등록에 실패한다")
        void createFailWhenQuestionDoesNotExist() {
            // given
            AnswerSaveRequest request =
                    new AnswerSaveRequest(
                            "답변 내용"
                    );

            given(questionRepository.findByIdAndIsActiveTrue(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    answerService.create(
                            999L,
                            10L,
                            request
                    )
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(QuestionErrorCode.QUESTION_NOT_FOUND)
                    );

            then(answerRepository)
                    .should(never())
                    .save(any(Answer.class));

            then(adminRepository)
                    .shouldHaveNoInteractions();

            then(eventPublisher)
                    .shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("활성 답변이 이미 존재하면 답변을 중복 등록할 수 없다")
        void createFailWhenActiveAnswerAlreadyExists() {
            // given
            Question question = Question.create(
                    "user@example.com",
                    "문의 제목",
                    "문의 내용"
            );

            AnswerSaveRequest request =
                    new AnswerSaveRequest(
                            "새로운 답변"
                    );

            given(questionRepository.findByIdAndIsActiveTrue(1L))
                    .willReturn(Optional.of(question));

            given(
                    answerRepository
                            .existsByQuestionIdAndIsActiveTrue(1L)
            )
                    .willReturn(true);

            // when & then
            assertThatThrownBy(() ->
                    answerService.create(
                            1L,
                            10L,
                            request
                    )
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(AnswerErrorCode.ANSWER_ALREADY_EXISTS)
                    );

            then(adminRepository)
                    .shouldHaveNoInteractions();

            then(answerRepository)
                    .should(never())
                    .save(any(Answer.class));

            then(eventPublisher)
                    .shouldHaveNoInteractions();

            assertThat(question.getStatus())
                    .isEqualTo(QuestionStatus.WAITING);
        }

        @Test
        @DisplayName("관리자가 존재하지 않으면 답변 등록에 실패한다")
        void createFailWhenAdminDoesNotExist() {
            // given
            Question question = Question.create(
                    "user@example.com",
                    "문의 제목",
                    "문의 내용"
            );

            AnswerSaveRequest request =
                    new AnswerSaveRequest(
                            "답변 내용"
                    );

            given(questionRepository.findByIdAndIsActiveTrue(1L))
                    .willReturn(Optional.of(question));

            given(
                    answerRepository
                            .existsByQuestionIdAndIsActiveTrue(1L)
            )
                    .willReturn(false);

            given(adminRepository.findById(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    answerService.create(
                            1L,
                            999L,
                            request
                    )
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(AuthErrorCode.UNAUTHORIZED)
                    );

            then(answerRepository)
                    .should(never())
                    .save(any(Answer.class));

            then(eventPublisher)
                    .shouldHaveNoInteractions();

            assertThat(question.getStatus())
                    .isEqualTo(QuestionStatus.WAITING);
        }
    }

    @Nested
    @DisplayName("답변 삭제")
    class DeleteAnswer {

        @Test
        @DisplayName("답변을 삭제하면 답변을 비활성화하고 문의 상태를 WAITING으로 변경한다")
        void deleteSuccess() {
            // given
            Question question = Question.create(
                    "user@example.com",
                    "문의 제목",
                    "문의 내용"
            );

            question.markAsAnswered();

            Admin admin = new Admin(
                    "admin",
                    "password"
            );

            Answer answer = Answer.create(
                    question,
                    "답변 내용",
                    admin
            );

            given(answerRepository.findByIdAndIsActiveTrue(1L))
                    .willReturn(Optional.of(answer));

            // when
            answerService.delete(1L);

            // then
            assertThat(answer.isActive())
                    .isFalse();

            assertThat(question.getStatus())
                    .isEqualTo(QuestionStatus.WAITING);

            then(answerRepository)
                    .should()
                    .findByIdAndIsActiveTrue(1L);
        }

        @Test
        @DisplayName("활성 답변이 존재하지 않으면 답변 삭제에 실패한다")
        void deleteFailWhenAnswerDoesNotExist() {
            // given
            given(answerRepository.findByIdAndIsActiveTrue(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    answerService.delete(999L)
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(AnswerErrorCode.ANSWER_NOT_FOUND)
                    );

            then(answerRepository)
                    .should()
                    .findByIdAndIsActiveTrue(999L);
        }
    }
}