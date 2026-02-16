package com.example.invoicing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class SaleDTO {

    private Long id;
    private CustomerDTO customer;
    private BigDecimal totalPrice;
    private BigDecimal paidAmount;
    private LocalDateTime createdAt;
    private String remark;
    private List<SaleItemDTO> items;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerDTO {
        private Long id;
        private String name;   // ✅ ADD THIS
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SaleItemDTO {
        private String productName;
    }
}