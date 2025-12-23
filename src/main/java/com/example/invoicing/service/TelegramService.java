package com.example.invoicing.service;

import java.util.List;

public interface TelegramService {
    void sendMessage(List<String> blocks);
}
