package com.example.invoicing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class SaleDTO {
    private Long id;
    private CustomerDTO customer;
    private BigDecimal totalPrice;
    private BigDecimal paidAmount;
    private LocalDateTime createdAt;
    private String remark;

    @Data
    public static class CustomerDTO {
        private Long id;
    }
}
