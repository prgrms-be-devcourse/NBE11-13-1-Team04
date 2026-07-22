package com.springbeans.cafemenumanagement.question.domain.repository;

import com.springbeans.cafemenumanagement.question.domain.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByEmailOrderByCreatedAtDesc(String email);
    // 정렬
    List<Question> findAllByOrderByCreatedAtDesc();
}