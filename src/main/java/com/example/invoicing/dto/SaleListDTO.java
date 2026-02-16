package com.example.invoicing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record SaleListDTO(
 Long id,
 Long customerId,
 String customerName,
 BigDecimal totalPrice,
 LocalDateTime createdAt
) {}