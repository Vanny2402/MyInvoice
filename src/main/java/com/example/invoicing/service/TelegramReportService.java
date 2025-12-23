package com.example.invoicing.service;

public interface TelegramReportService {

    /**
     * Build and send the current month sales report to Telegram.
     * The report is automatically split into multiple messages if needed.
     */
    void sendMonthlyReport();
}
