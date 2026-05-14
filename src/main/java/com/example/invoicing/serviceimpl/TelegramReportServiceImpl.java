package com.example.invoicing.serviceimpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.invoicing.dto.TelegramSaleReportDTO;
import com.example.invoicing.dto.SaleTelegramDTO;
import com.example.invoicing.repository.SaleRepository;
import com.example.invoicing.service.TelegramReportService;
import com.example.invoicing.service.TelegramService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TelegramReportServiceImpl implements TelegramReportService {

    private final SaleRepository saleRepository;
    private final TelegramService telegramService;

    @Override
    public void sendMonthlyReport() {
        ZoneId zone = ZoneId.of("Asia/Phnom_Penh");
        LocalDateTime start = LocalDate.now(zone)
                .withDayOfMonth(1)
                .atStartOfDay();
        LocalDateTime end = start.plusMonths(1);

        // 1️⃣ Load data
        List<TelegramSaleReportDTO> invoices =
                saleRepository.findInvoicesForMonth(start, end);

        if (invoices.isEmpty()) {
            return;
        }

        List<SaleTelegramDTO> items =
                saleRepository.findInvoiceItemsForMonth(start, end);

        // 2️⃣ Totals
        Object[] row = (Object[]) saleRepository.sumTotalsForMonth(start, end);

        BigDecimal total = row[0] != null ? (BigDecimal) row[0] : BigDecimal.ZERO;
        BigDecimal paid  = row[1] != null ? (BigDecimal) row[1] : BigDecimal.ZERO;
        BigDecimal debt  = total.subtract(paid);

        // 3️⃣ Build TELEGRAM-SAFE blocks
        List<String> blocks = TelegramReportFormatter.formatMonthlyBlocks(
                invoices,
                items,
                total,
                paid,
                debt
        );

        // 4️⃣ Send blocks (auto-split inside TelegramService)
        telegramService.sendMessage(blocks);
    }

}
