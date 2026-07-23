package com.springbeans.cafemenumanagement.global.slack;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Slf4j
@Service
public class SlackNotificationService {

    @Value("${slack.webhook.url:}")
    private String slackWebhookUrl;

    private final RestClient restClient = RestClient.create();

    /**
     * 슬랙 전송 공통 메인 메서드
     */
    public void sendSlackMessage(Map<String, Object> payload) {
        if (slackWebhookUrl == null || slackWebhookUrl.isBlank()) {
            log.warn("Slack Webhook URL이 설정되지 않아 알림을 보내지 않습니다.");
            return;
        }

        try {
            restClient.post()
                    .uri(slackWebhookUrl)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.error("Slack notification send failed: {}", e.getMessage());
        }
    }

    // ==========================================
    // 1. 에러 알림
    // ==========================================
    public void sendErrorNotification(String apiPath, String errorMessage, String stackTrace) {
        Map<String, Object> payload = Map.of(
                "text", "🚨 *[시스템 에러 발생]*\n" +
                        "• *API 경로:* `" + apiPath + "`\n" +
                        "• *에러 메시지:* " + errorMessage + "\n" +
                        "• *스택 트레이스:* ```" + truncateText(stackTrace, 500) + "```"
                                            );
        sendSlackMessage(payload);
    }

    // ==========================================
    // 2. 문의하기 알림
    // ==========================================
    public void sendInquiryNotification(String userEmail, String title, String content) {
        Map<String, Object> payload = Map.of(
                "text", "💬 *[새로운 고객 문의 접수]*\n" +
                        "• *작성자 (이메일):* " + userEmail + "\n" +
                        "• *제목:* " + title + "\n" +
                        "• *내용:* " + content
                                            );
        sendSlackMessage(payload);
    }

    // ==========================================
    // 3. 스케줄러 일괄 주문 확정 건수 알림
    // ==========================================
    public void sendBatchConfirmNotification(int count) {
        Map<String, Object> payload = Map.of(
                "text", "📦 *[주문 자동 확정 완료]*\n" +
                        "• *총 확정 처리 건수:* *" + count + "건*"
                                            );
        sendSlackMessage(payload);
    }

    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        return text.length() > maxLength ? text.substring(0, maxLength) + "..." : text;
    }
}