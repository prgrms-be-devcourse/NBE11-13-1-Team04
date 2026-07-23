package com.springbeans.cafemenumanagement.question.service;

import com.springbeans.cafemenumanagement.answer.domain.repository.AnswerRepository;
import com.springbeans.cafemenumanagement.global.exception.BusinessException;
import com.springbeans.cafemenumanagement.global.slack.SlackNotificationService;
import com.springbeans.cafemenumanagement.question.domain.entity.Question;
import com.springbeans.cafemenumanagement.question.domain.entity.QuestionStatus;
import com.springbeans.cafemenumanagement.question.domain.repository.QuestionRepository;
import com.springbeans.cafemenumanagement.question.dto.request.QuestionAuthRequest;
import com.springbeans.cafemenumanagement.question.dto.request.QuestionCreateRequest;
import com.springbeans.cafemenumanagement.question.dto.response.AdminQuestionDetailResponse;
import com.springbeans.cafemenumanagement.question.dto.response.AdminQuestionPreviewResponse;
import com.springbeans.cafemenumanagement.question.dto.response.QuestionDetailResponse;
import com.springbeans.cafemenumanagement.question.dto.response.QuestionPreviewResponse;
import com.springbeans.cafemenumanagement.question.dto.response.QuestionSaveResponse;
import com.springbeans.cafemenumanagement.question.exception.QuestionErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private SlackNotificationService slackNotificationService;

    private QuestionService questionService;

    @BeforeEach
    void setUp() {
        questionService = new QuestionService(
                questionRepository,
                answerRepository,
                slackNotificationService
        );
    }

    @Nested
    @DisplayName("문의 등록")
    class CreateQuestion {

        @Test
        @DisplayName("유효한 요청이면 문의를 WAITING 상태로 등록한다")
        void createSuccess() {
            // given
            QuestionCreateRequest request = new QuestionCreateRequest(
                    "user@example.com",
                    "주문 문의",
                    "주문 상태를 확인하고 싶습니다."
            );

            given(questionRepository.save(any(Question.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            QuestionSaveResponse response =
                    questionService.create(request);

            // then
            ArgumentCaptor<Question> questionCaptor =
                    ArgumentCaptor.forClass(Question.class);

            then(questionRepository)
                    .should()
                    .save(questionCaptor.capture());

            Question savedQuestion = questionCaptor.getValue();

            assertThat(savedQuestion.getEmail())
                    .isEqualTo("user@example.com");

            assertThat(savedQuestion.getTitle())
                    .isEqualTo("주문 문의");

            assertThat(savedQuestion.getContent())
                    .isEqualTo("주문 상태를 확인하고 싶습니다.");

            assertThat(savedQuestion.getStatus())
                    .isEqualTo(QuestionStatus.WAITING);

            assertThat(savedQuestion.isActive())
                    .isTrue();

            assertThat(savedQuestion.getCreatedAt())
                    .isNotNull();

            assertThat(response)
                    .isNotNull();

            then(slackNotificationService)
                    .should()
                    .sendInquiryNotification(
                            "user@example.com",
                            "주문 문의",
                            "주문 상태를 확인하고 싶습니다."
                    );
        }

        @Test
        @DisplayName("문의 등록 요청이 null이면 예외가 발생한다")
        void createFailWhenRequestIsNull() {
            // when & then
            assertThatThrownBy(() ->
                    questionService.create(null)
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(QuestionErrorCode.QUESTION_REQUEST_REQUIRED)
                    );

            then(questionRepository)
                    .should(never())
                    .save(any(Question.class));

            then(slackNotificationService)
                    .shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("이메일이 공백이면 문의 등록에 실패한다")
        void createFailWhenEmailIsBlank() {
            // given
            QuestionCreateRequest request = new QuestionCreateRequest(
                    " ",
                    "문의 제목",
                    "문의 내용"
            );

            // when & then
            assertThatThrownBy(() ->
                    questionService.create(request)
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(QuestionErrorCode.QUESTION_EMAIL_REQUIRED)
                    );

            then(questionRepository)
                    .should(never())
                    .save(any(Question.class));

            then(slackNotificationService)
                    .shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("제목이 공백이면 문의 등록에 실패한다")
        void createFailWhenTitleIsBlank() {
            // given
            QuestionCreateRequest request = new QuestionCreateRequest(
                    "user@example.com",
                    " ",
                    "문의 내용"
            );

            // when & then
            assertThatThrownBy(() ->
                    questionService.create(request)
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(QuestionErrorCode.QUESTION_TITLE_REQUIRED)
                    );

            then(questionRepository)
                    .should(never())
                    .save(any(Question.class));

            then(slackNotificationService)
                    .shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("문의 내용이 공백이면 문의 등록에 실패한다")
        void createFailWhenContentIsBlank() {
            // given
            QuestionCreateRequest request = new QuestionCreateRequest(
                    "user@example.com",
                    "문의 제목",
                    " "
            );

            // when & then
            assertThatThrownBy(() ->
                    questionService.create(request)
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(QuestionErrorCode.QUESTION_CONTENT_REQUIRED)
                    );

            then(questionRepository)
                    .should(never())
                    .save(any(Question.class));

            then(slackNotificationService)
                    .shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("사용자 문의 목록 조회")
    class GetQuestions {

        @Test
        @DisplayName("이메일에 해당하는 활성 문의 목록을 반환한다")
        void getQuestionsSuccess() {
            // given
            Question firstQuestion = Question.create(
                    "user@example.com",
                    "첫 번째 문의",
                    "첫 번째 문의 내용"
            );

            Question secondQuestion = Question.create(
                    "user@example.com",
                    "두 번째 문의",
                    "두 번째 문의 내용"
            );

            given(
                    questionRepository
                            .findByEmailAndIsActiveTrueOrderByCreatedAtDesc(
                                    "user@example.com"
                            )
            )
                    .willReturn(
                            List.of(secondQuestion, firstQuestion)
                    );

            // when
            List<QuestionPreviewResponse> responses =
                    questionService.getQuestions(
                            "user@example.com"
                    );

            // then
            assertThat(responses)
                    .hasSize(2);

            then(questionRepository)
                    .should()
                    .findByEmailAndIsActiveTrueOrderByCreatedAtDesc(
                            "user@example.com"
                    );
        }

        @Test
        @DisplayName("등록된 문의가 없으면 빈 목록을 반환한다")
        void getQuestionsReturnsEmptyList() {
            // given
            given(
                    questionRepository
                            .findByEmailAndIsActiveTrueOrderByCreatedAtDesc(
                                    "user@example.com"
                            )
            )
                    .willReturn(List.of());

            // when
            List<QuestionPreviewResponse> responses =
                    questionService.getQuestions(
                            "user@example.com"
                    );

            // then
            assertThat(responses)
                    .isNotNull()
                    .isEmpty();

            then(questionRepository)
                    .should()
                    .findByEmailAndIsActiveTrueOrderByCreatedAtDesc(
                            "user@example.com"
                    );
        }
    }

    @Nested
    @DisplayName("사용자 문의 상세 조회")
    class GetQuestion {

        @Test
        @DisplayName("작성자 이메일이 일치하면 답변이 없는 문의 상세를 반환한다")
        void getQuestionSuccessWithoutAnswer() {
            // given
            Question question = Question.create(
                    "user@example.com",
                    "문의 제목",
                    "문의 내용"
            );

            given(questionRepository.findByIdAndIsActiveTrue(1L))
                    .willReturn(Optional.of(question));

            given(answerRepository.findByQuestionIdAndIsActiveTrue(1L))
                    .willReturn(Optional.empty());

            // when
            QuestionDetailResponse response =
                    questionService.getQuestion(
                            1L,
                            "user@example.com"
                    );

            // then
            assertThat(response)
                    .isNotNull();

            then(questionRepository)
                    .should()
                    .findByIdAndIsActiveTrue(1L);

            then(answerRepository)
                    .should()
                    .findByQuestionIdAndIsActiveTrue(1L);
        }

        @Test
        @DisplayName("이메일이 공백이면 문의 상세 조회에 실패한다")
        void getQuestionFailWhenEmailIsBlank() {
            // when & then
            assertThatThrownBy(() ->
                    questionService.getQuestion(1L, " ")
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(QuestionErrorCode.QUESTION_EMAIL_REQUIRED)
                    );

            then(questionRepository)
                    .shouldHaveNoInteractions();

            then(answerRepository)
                    .shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("활성 문의가 존재하지 않으면 상세 조회에 실패한다")
        void getQuestionFailWhenQuestionDoesNotExist() {
            // given
            given(questionRepository.findByIdAndIsActiveTrue(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    questionService.getQuestion(
                            999L,
                            "user@example.com"
                    )
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(QuestionErrorCode.QUESTION_NOT_FOUND)
                    );

            then(answerRepository)
                    .shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("문의 작성자 이메일이 다르면 상세 조회에 실패한다")
        void getQuestionFailWhenEmailDoesNotMatch() {
            // given
            Question question = Question.create(
                    "writer@example.com",
                    "문의 제목",
                    "문의 내용"
            );

            given(questionRepository.findByIdAndIsActiveTrue(1L))
                    .willReturn(Optional.of(question));

            // when & then
            assertThatThrownBy(() ->
                    questionService.getQuestion(
                            1L,
                            "other@example.com"
                    )
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(QuestionErrorCode.QUESTION_ACCESS_DENIED)
                    );

            then(answerRepository)
                    .shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("관리자 문의 조회")
    class GetAdminQuestions {

        @Test
        @DisplayName("관리자는 전체 활성 문의 목록을 조회한다")
        void getAdminQuestionsSuccess() {
            // given
            Question question = Question.create(
                    "user@example.com",
                    "문의 제목",
                    "문의 내용"
            );

            given(
                    questionRepository
                            .findAllByIsActiveTrueOrderByCreatedAtDesc()
            )
                    .willReturn(List.of(question));

            // when
            List<AdminQuestionPreviewResponse> responses =
                    questionService.getAdminQuestions();

            // then
            assertThat(responses)
                    .hasSize(1);

            then(questionRepository)
                    .should()
                    .findAllByIsActiveTrueOrderByCreatedAtDesc();
        }

        @Test
        @DisplayName("활성 문의가 없으면 빈 목록을 반환한다")
        void getAdminQuestionsReturnsEmptyList() {
            // given
            given(
                    questionRepository
                            .findAllByIsActiveTrueOrderByCreatedAtDesc()
            )
                    .willReturn(List.of());

            // when
            List<AdminQuestionPreviewResponse> responses =
                    questionService.getAdminQuestions();

            // then
            assertThat(responses)
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        @DisplayName("관리자는 답변이 없는 문의 상세를 조회한다")
        void getAdminQuestionSuccessWithoutAnswer() {
            // given
            Question question = Question.create(
                    "user@example.com",
                    "문의 제목",
                    "문의 내용"
            );

            given(questionRepository.findByIdAndIsActiveTrue(1L))
                    .willReturn(Optional.of(question));

            given(answerRepository.findByQuestionIdAndIsActiveTrue(1L))
                    .willReturn(Optional.empty());

            // when
            AdminQuestionDetailResponse response =
                    questionService.getAdminQuestion(1L);

            // then
            assertThat(response)
                    .isNotNull();

            then(questionRepository)
                    .should()
                    .findByIdAndIsActiveTrue(1L);

            then(answerRepository)
                    .should()
                    .findByQuestionIdAndIsActiveTrue(1L);
        }

        @Test
        @DisplayName("활성 문의가 존재하지 않으면 관리자 상세 조회에 실패한다")
        void getAdminQuestionFailWhenQuestionDoesNotExist() {
            // given
            given(questionRepository.findByIdAndIsActiveTrue(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    questionService.getAdminQuestion(999L)
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(QuestionErrorCode.QUESTION_NOT_FOUND)
                    );

            then(answerRepository)
                    .shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("문의 삭제")
    class DeleteQuestion {

        @Test
        @DisplayName("작성자 이메일이 일치하고 활성 답변이 없으면 문의를 비활성화한다")
        void deleteSuccessWithoutAnswer() {
            // given
            Question question = Question.create(
                    "user@example.com",
                    "문의 제목",
                    "문의 내용"
            );

            QuestionAuthRequest request =
                    new QuestionAuthRequest(
                            "user@example.com"
                    );

            given(questionRepository.findByIdAndIsActiveTrue(1L))
                    .willReturn(Optional.of(question));

            given(answerRepository.findByQuestionIdAndIsActiveTrue(1L))
                    .willReturn(Optional.empty());

            // when
            questionService.delete(1L, request);

            // then
            assertThat(question.isActive())
                    .isFalse();

            then(questionRepository)
                    .should()
                    .findByIdAndIsActiveTrue(1L);

            then(answerRepository)
                    .should()
                    .findByQuestionIdAndIsActiveTrue(1L);
        }

        @Test
        @DisplayName("삭제 요청이 null이면 문의 삭제에 실패한다")
        void deleteFailWhenRequestIsNull() {
            // when & then
            assertThatThrownBy(() ->
                    questionService.delete(1L, null)
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(QuestionErrorCode.QUESTION_AUTH_REQUEST_REQUIRED)
                    );

            then(questionRepository)
                    .shouldHaveNoInteractions();

            then(answerRepository)
                    .shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("삭제 요청 이메일이 공백이면 문의 삭제에 실패한다")
        void deleteFailWhenEmailIsBlank() {
            // given
            QuestionAuthRequest request =
                    new QuestionAuthRequest(" ");

            // when & then
            assertThatThrownBy(() ->
                    questionService.delete(1L, request)
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(QuestionErrorCode.QUESTION_EMAIL_REQUIRED)
                    );

            then(questionRepository)
                    .shouldHaveNoInteractions();

            then(answerRepository)
                    .shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("활성 문의가 존재하지 않으면 삭제에 실패한다")
        void deleteFailWhenQuestionDoesNotExist() {
            // given
            QuestionAuthRequest request =
                    new QuestionAuthRequest(
                            "user@example.com"
                    );

            given(questionRepository.findByIdAndIsActiveTrue(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() ->
                    questionService.delete(999L, request)
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(QuestionErrorCode.QUESTION_NOT_FOUND)
                    );

            then(answerRepository)
                    .shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("문의 작성자 이메일이 다르면 삭제에 실패한다")
        void deleteFailWhenEmailDoesNotMatch() {
            // given
            Question question = Question.create(
                    "writer@example.com",
                    "문의 제목",
                    "문의 내용"
            );

            QuestionAuthRequest request =
                    new QuestionAuthRequest(
                            "other@example.com"
                    );

            given(questionRepository.findByIdAndIsActiveTrue(1L))
                    .willReturn(Optional.of(question));

            // when & then
            assertThatThrownBy(() ->
                    questionService.delete(1L, request)
            )
                    .isInstanceOfSatisfying(
                            BusinessException.class,
                            exception -> assertThat(exception.getErrorCode())
                                    .isEqualTo(QuestionErrorCode.QUESTION_DELETE_ACCESS_DENIED)
                    );

            assertThat(question.isActive())
                    .isTrue();

            then(answerRepository)
                    .shouldHaveNoInteractions();
        }
    }
}