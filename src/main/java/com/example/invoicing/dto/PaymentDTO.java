package com.example.invoicing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;


@Data
public class PaymentDTO {
    private Long id;
    private CustomerDTO customer;
    private BigDecimal amount;
    private String remark;
    private LocalDateTime paymentDate;

    // getters and setters
    @Data
    public static class CustomerDTO {
        private Long id;
        // getters and setters
    }
}
