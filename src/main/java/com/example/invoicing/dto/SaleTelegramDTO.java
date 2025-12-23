// src/main/java/com/example/invoicing/dto/SaleTelegramDTO.java
package com.example.invoicing.dto;

import java.math.BigDecimal;

public class SaleTelegramDTO {
    private final Long saleId;
    private final String productName;
    private final BigDecimal qty;
    private final BigDecimal price;
    private final BigDecimal lineTotal;

    public SaleTelegramDTO(Long saleId, String productName, BigDecimal qty, BigDecimal price, BigDecimal lineTotal) {
        this.saleId = saleId;
        this.productName = productName;
        this.qty = qty;
        this.price = price;
        this.lineTotal = lineTotal;
    }

    public Long getSaleId() { return saleId; }
    public String getProductName() { return productName; }
    public BigDecimal getQty() { return qty; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getLineTotal() { return lineTotal; }
}
