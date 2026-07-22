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

    // 하나의 문의에 하나의 답변만 등록할 수 있도록 일대일 관계를 설정함
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "question_id",
            nullable = false,
            unique = true
    )
    private Question question;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    // 한 명의 관리자가 여러 답변을 작성할 수 있도록 다대일 관계를 설정함
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private Admin admin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private Answer(
            Question question,
            String content,
            Admin admin
    ) {
        this.question = question;
        this.content = content;
        this.admin = admin;
        this.createdAt = LocalDateTime.now();
    }

    public static Answer create(
            Question question,
            String content,
            Admin admin
    ) {
        return new Answer(question, content, admin);
    }
}