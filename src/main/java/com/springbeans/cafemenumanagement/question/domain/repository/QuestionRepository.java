package com.springbeans.cafemenumanagement.question.domain.repository;

import com.springbeans.cafemenumanagement.question.domain.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 사용자의 활성 문의 목록 조회
    List<Question> findByEmailAndIsActiveTrueOrderByCreatedAtDesc(String email);

    // 관리자의 전체 활성 문의 목록 조회
    List<Question> findAllByIsActiveTrueOrderByCreatedAtDesc();

    // 활성 문의 단건 조회
    Optional<Question> findByIdAndIsActiveTrue(Long questionId);
}