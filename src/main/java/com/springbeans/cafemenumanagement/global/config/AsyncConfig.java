package com.springbeans.cafemenumanagement.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {
    // Spring Bean 중에서 @Async가 붙은 메서드를 찾아 호출이 들어오면 별도의 스레드에서 실행
}