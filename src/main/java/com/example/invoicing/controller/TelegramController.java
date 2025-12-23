package com.example.invoicing.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.invoicing.service.TelegramService;
import com.example.invoicing.service.TelegramReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class TelegramController {

    private final TelegramReportService telegramReportService;
    @GetMapping("/sales/monthly/telegram")
    public ResponseEntity<String> sendMonthlySalesToTelegram() {
        telegramReportService.sendMonthlyReport();
        return ResponseEntity.ok("Monthly sales sent to Telegram.");
    }

}
