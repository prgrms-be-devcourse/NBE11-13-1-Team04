package com.springbeans.cafemenumanagement.answer.domain.entity;

import com.springbeans.cafemenumanagement.admin.auth.domain.entity.Admin;
import com.springbeans.cafemenumanagement.question.domain.entity.Question;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "answers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 하나의 문의에 여러 답변 이력 존재 가능
    // 단, 활성 상태의 답변은 최대 1개만 존재 가능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 한 명의 관리자가 여러 답변을 작성할 수 있도록 다대일 관계를 설정함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    private Answer(
            Question question,
            String content,
            Admin admin
    ) {
        this.question = question;
        this.content = content;
        this.admin = admin;
        this.createdAt = LocalDateTime.now();
        this.isActive = true;
    }

    public static Answer create(
            Question question,
            String content,
            Admin admin
    ) {
        return new Answer(question, content, admin);
    }

    // 답변 비활성화
    public void deactivate() {
        this.isActive = false;
    }
}