package com.springbeans.cafemenumanagement.answer.domain.repository;

import com.springbeans.cafemenumanagement.answer.domain.entity.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {

    Optional<Answer> findByQuestionId(Long questionId);

    boolean existsByQuestionId(Long questionId);
}