package com.example.invoicing.dto;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseSummaryDTO {
    private Long id;
    private String supplier;
    private ZonedDateTime createdAt;
    private BigDecimal totalPrice;
    private List<ItemDTO> items;
}