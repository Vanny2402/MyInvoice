// src/main/java/com/example/invoicing/dto/TelegramSaleReportDTO.java
package com.example.invoicing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TelegramSaleReportDTO {
    private final Long id;
    private final String customerName;
    private final LocalDateTime createdAt;
    private final BigDecimal totalPrice;
    private final BigDecimal paidAmount;
    private final List<SaleTelegramDTO> items = new ArrayList<>();

    public TelegramSaleReportDTO(Long id, String customerName, LocalDateTime createdAt,
                                 BigDecimal totalPrice, BigDecimal paidAmount) {
        this.id = id;
        this.customerName = customerName;
        this.createdAt = createdAt;
        this.totalPrice = totalPrice;
        this.paidAmount = paidAmount;
    }

    public Long getId() { return id; }
    public String getCustomerName() { return customerName; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public BigDecimal getPaidAmount() { return paidAmount; }
    public List<SaleTelegramDTO> getItems() { return items; }
    
    
}
