package com.example.invoicing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class TelegramWebClientConfig {

    public static final String TELEGRAM_WEB_CLIENT = "telegramWebClient";

    @Bean(name = TELEGRAM_WEB_CLIENT)
    public WebClient telegramWebClient(WebClient.Builder builder) {
        return builder.baseUrl("https://api.telegram.org").build();
    }
}
