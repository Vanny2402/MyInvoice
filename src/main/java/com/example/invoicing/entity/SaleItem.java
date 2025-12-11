package com.example.invoicing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "sale_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JsonBackReference
    private Sale sale;

    @ManyToOne
    private Product product;

    private BigDecimal qty = BigDecimal.ZERO;
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "line_total", precision = 14, scale = 2)
    private BigDecimal lineTotal = BigDecimal.ZERO;

    @PrePersist
    @PreUpdate
    public void computeLineTotal() {
        if (qty == null) qty = BigDecimal.ZERO;
        if (price == null) price = BigDecimal.ZERO;
        this.lineTotal = qty.multiply(price);
    }
}
