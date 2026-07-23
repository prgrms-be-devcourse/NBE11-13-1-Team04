package com.springbeans.cafemenumanagement.answer.event;

public record AnswerCreatedEvent(
        String recipientEmail,
        String questionTitle,
        String answerContent
) {

}

