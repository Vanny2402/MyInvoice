package com.example.invoicing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SaleWithItemDTO {
    private Long saleId;
    private Long customerId;
    private String customerName;
    private String productName;
    private int qty;
    private BigDecimal price;
    private BigDecimal lineTotal;
    private BigDecimal totalPrice;
    private LocalDateTime createdAt;

    // ✅ Constructor matching the query
    public SaleWithItemDTO(Long saleId,
                           Long customerId,
                           String customerName,
                           String productName,
                           int qty,
                           BigDecimal price,
                           BigDecimal lineTotal,
                           BigDecimal totalPrice,
                           LocalDateTime createdAt) {
        this.saleId = saleId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.productName = productName;
        this.qty = qty;
        this.price = price;
        this.lineTotal = lineTotal;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    // ✅ Getters (needed for JSON serialization)
    public Long getSaleId() { return saleId; }
    public Long getCustomerId() { return customerId; }
    public String getCustomerName() { return customerName; }
    public String getProductName() { return productName; }
    public int getQty() { return qty; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getLineTotal() { return lineTotal; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
