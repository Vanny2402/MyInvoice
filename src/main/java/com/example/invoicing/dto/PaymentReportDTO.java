package com.example.invoicing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentReportDTO {

    private Long id;
    private CustomerNameDTO customer;
    private BigDecimal amount;
    private String remark;
    private LocalDateTime paymentDate;

    // ✅ JPQL constructor
    public PaymentReportDTO(
            Long id,
            String customerName,
            BigDecimal amount,
            String remark,
            LocalDateTime paymentDate
    ) {
        this.id = id;
        this.customer = new CustomerNameDTO(customerName);
        this.amount = amount;
        this.remark = remark;
        this.paymentDate = paymentDate;
    }

    public Long getId() { return id; }
    public CustomerNameDTO getCustomer() { return customer; }
    public BigDecimal getAmount() { return amount; }
    public String getRemark() { return remark; }
    public LocalDateTime getPaymentDate() { return paymentDate; }
}
