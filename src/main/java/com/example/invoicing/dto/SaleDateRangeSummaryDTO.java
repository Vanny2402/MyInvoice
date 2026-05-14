package com.example.invoicing.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaleDateRangeSummaryDTO {
    /** Sum of sale totals in the date range (all pages). */
    private BigDecimal totalSales;
    /** Sum of qty × product.purchasePrice for all line items in range (matches list “cost” logic). */
    private BigDecimal totalPurchaseCost;
}
