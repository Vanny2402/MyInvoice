package com.example.invoicing.serviceimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.example.invoicing.config.TelegramConfig;
import com.example.invoicing.service.TelegramService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TelegramServiceImpl implements TelegramService {

    private final TelegramConfig telegramConfig;

    
    private static final int TELEGRAM_LIMIT = 4096;

    /**
     * Splits text into chunks <= 4096 chars
     * Keeps <pre> blocks safe
     */
    private List<String> splitTelegramMessage(String text) {
        List<String> parts = new ArrayList<>();

        while (text.length() > TELEGRAM_LIMIT) {
            int splitAt = text.lastIndexOf('\n', TELEGRAM_LIMIT);

            if (splitAt <= 0) {
                splitAt = TELEGRAM_LIMIT;
            }

            parts.add(text.substring(0, splitAt));
            text = text.substring(splitAt);
        }

        parts.add(text);
        return parts;
    }

    public void sendMessage(List<String> blocks) {
        String token = telegramConfig.getBotToken();
        String chatId = telegramConfig.getChatId();
        String url = "https://api.telegram.org/bot" + token + "/sendMessage";

        WebClient client = WebClient.create(url);

        StringBuilder current = new StringBuilder();

        for (String block : blocks) {
            if (current.length() + block.length() > TELEGRAM_LIMIT) {
                sendOne(client, chatId, current.toString());
                current.setLength(0);
            }
            current.append(block).append("\n");
        }

        if (!current.isEmpty()) {
            sendOne(client, chatId, current.toString());
        }
    }

    private void sendOne(WebClient client, String chatId, String text) {
        client.post()
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .bodyValue("chat_id=" + chatId +
                       "&text=" + text +
                       "&parse_mode=HTML")
            .retrieve()
            .bodyToMono(String.class)
            .block();
    }



}
//.bodyValue("chat_id=" + chatId + "&text=" + text + "&parse_mode=HTML")
