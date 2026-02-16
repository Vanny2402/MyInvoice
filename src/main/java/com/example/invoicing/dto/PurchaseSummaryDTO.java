package com.example.invoicing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

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
