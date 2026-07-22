package com.springbeans.cafemenumanagement.question.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Getter
@NoArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionStatus status;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private Question(
            String email,
            String title,
            String content
    ) {
        this.email = email;
        this.title = title;
        this.content = content;
        this.status = QuestionStatus.WAITING;
        this.createdAt = LocalDateTime.now();
    }

    public static Question create(
            String email,
            String title,
            String content
    ) {
        return new Question(email, title, content);
    }

    // 답변 등록 시 상태 변경
    public void markAsAnswered() {
        this.status = QuestionStatus.ANSWERED;
    }
}