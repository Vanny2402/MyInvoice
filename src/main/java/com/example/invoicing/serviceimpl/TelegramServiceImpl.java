package com.example.invoicing.serviceimpl;

import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.invoicing.config.TelegramConfig;
import com.example.invoicing.config.TelegramWebClientConfig;
import com.example.invoicing.service.TelegramService;

@Service
public class TelegramServiceImpl implements TelegramService {

    private static final int TELEGRAM_LIMIT = 4096;
    private static final Duration TELEGRAM_TIMEOUT = Duration.ofSeconds(45);

    private final TelegramConfig telegramConfig;
    private final WebClient telegramWebClient;

    public TelegramServiceImpl(
            TelegramConfig telegramConfig,
            @Qualifier(TelegramWebClientConfig.TELEGRAM_WEB_CLIENT) WebClient telegramWebClient) {
        this.telegramConfig = telegramConfig;
        this.telegramWebClient = telegramWebClient;
    }

    @Override
    public void sendMessage(List<String> blocks) {
        if (blocks == null || blocks.isEmpty()) {
            return;
        }

        String token = telegramConfig.getBotToken();
        String chatId = telegramConfig.getChatId();

        StringBuilder current = new StringBuilder();

        for (String block : blocks) {
            if (current.length() + block.length() > TELEGRAM_LIMIT) {
                sendOne(token, chatId, current.toString());
                current.setLength(0);
            }
            current.append(block).append("\n");
        }

        if (!current.isEmpty()) {
            sendOne(token, chatId, current.toString());
        }
    }

    private void sendOne(String token, String chatId, String text) {
        telegramWebClient
                .post()
                .uri("/bot{token}/sendMessage", token)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData("chat_id", chatId)
                        .with("text", text)
                        .with("parse_mode", "HTML"))
                .retrieve()
                .bodyToMono(String.class)
                .block(TELEGRAM_TIMEOUT);
    }
}
