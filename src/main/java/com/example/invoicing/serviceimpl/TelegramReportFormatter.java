package com.example.invoicing.serviceimpl;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.example.invoicing.dto.SaleTelegramDTO;
import com.example.invoicing.dto.TelegramSaleReportDTO;

public final class TelegramReportFormatter {

    private TelegramReportFormatter() {}

    public static List<String> formatMonthlyBlocks(
            List<TelegramSaleReportDTO> invoices,
            List<SaleTelegramDTO> items,
            BigDecimal total,
            BigDecimal paid,
            BigDecimal debt
    ) {
        List<String> blocks = new ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // HEADER
        StringBuilder header = new StringBuilder();
        header.append("<b>📊 ការលក់ខែនេះ</b>\n\n");
        header.append("━━━━━━━━━━━━━━━━━\n");
        header.append("💰 ទឹកប្រាក់សរុប: ").append(total).append("$\n");
        header.append("━━━━━━━━━━━━━━━━━\n");
        blocks.add(header.toString());

        for (TelegramSaleReportDTO inv : invoices) {
            StringBuilder sb = new StringBuilder();

            sb.append("👤 <b>អ្នកទិញ : ")
              .append(esc(inv.getCustomerName()))
              .append("</b>");
            sb.append("🧾 លេខលក់# ").append(inv.getId()).append("\n");
            sb.append("💵 តម្លៃសរុប: ").append(inv.getTotalPrice()).append("$\n");
            sb.append("📅 កាលបរិច្ឆេទ: ")
              .append(inv.getCreatedAt() != null ? inv.getCreatedAt().format(dtf) : "-")
              .append("\n");

            sb.append("<pre>");
            sb.append(String.format("%-15s %-4s %-6s\n",
                    "ផលិតផល", "ចំនួន |", "សរុប"));
            sb.append("-----------------------------------\n");

            for (SaleTelegramDTO item : items) {
                if (!item.getSaleId().equals(inv.getId())) continue;

                String name = esc(item.getProductName());
                if (name.length() > 15) name = name.substring(0, 12) + "...";

                sb.append(String.format(
                        "%-15s %-4s %-6s\n",
                        name,
                        item.getQty()+" | ",
                        item.getLineTotal() + "$"
                ));
            }

          
            sb.append("</pre>\n");

            blocks.add(sb.toString());
        }

        return blocks;
    }

    private static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
