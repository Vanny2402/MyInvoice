package com.example.invoicing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "sales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<SaleItem> items;   // ✅ multiple products per sale
    private BigDecimal totalPrice = BigDecimal.ZERO;
    private BigDecimal paidAmount = BigDecimal.ZERO;
    private BigDecimal debt;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String remark;
    
    @PrePersist
    @PreUpdate
    public void computeTotal() {
        if (items != null && !items.isEmpty()) {
            this.totalPrice = items.stream()
                    .map(SaleItem::getLineTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            this.totalPrice = BigDecimal.ZERO;
        }
    }
}
