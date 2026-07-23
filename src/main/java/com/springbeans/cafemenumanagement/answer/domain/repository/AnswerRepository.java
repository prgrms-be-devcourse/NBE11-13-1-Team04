package com.springbeans.cafemenumanagement.answer.domain.repository;

import com.springbeans.cafemenumanagement.answer.domain.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    // 특정 문의에 등록된 활성 답변 단건 조회
    Optional<Answer> findByQuestionIdAndIsActiveTrue(Long questionId);

    // 답변 id로 활성 답변 단건 조회
    Optional<Answer> findByIdAndIsActiveTrue(Long answerId);

    // 특정 문의에 등록된 활성 답변 존재 여부 확인
    boolean existsByQuestionIdAndIsActiveTrue(Long questionId);
}