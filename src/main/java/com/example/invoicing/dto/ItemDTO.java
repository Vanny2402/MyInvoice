package com.example.invoicing.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDTO {
    private String name;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal lineTotal;
}