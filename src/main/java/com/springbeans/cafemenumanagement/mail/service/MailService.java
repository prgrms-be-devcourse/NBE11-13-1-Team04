package com.springbeans.cafemenumanagement.mail.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${MAIL_USERNAME}")
    private String sender;

    public void sendMail(String recipient, String questionTitle, String answerContent) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(sender);
        message.setTo(recipient);
        message.setSubject("[문의 답변 완료] " + questionTitle);
        message.setText("""
                문의하신 내용에 답변이 등록되었습니다.

                문의 제목
                %s

                답변 내용
                %s
                """.formatted(questionTitle, answerContent));

        mailSender.send(message);
    }
}
