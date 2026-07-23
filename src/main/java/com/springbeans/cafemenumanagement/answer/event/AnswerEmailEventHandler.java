package com.springbeans.cafemenumanagement.answer.event;

import com.springbeans.cafemenumanagement.mail.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class AnswerEmailEventHandler {
    private final MailService mailService;

    @Async
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(AnswerCreatedEvent event) {
        try {
            mailService.sendMail(
                    event.recipientEmail(),
                    event.questionTitle(),
                    event.answerContent()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
