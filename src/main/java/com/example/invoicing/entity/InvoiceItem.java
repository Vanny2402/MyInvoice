package com.example.invoicing.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "invoiceItem")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Invoice invoice;

    @ManyToOne
    private Product product;

    private Integer quantity;
    private Double price;
    private Double subtotal;

    @PrePersist
    public void decreaseStock() {
        if (product.getStock() < quantity) {
            throw new IllegalStateException("Not enough stock for product: " + product.getName());
        }
        product.setStock(product.getStock() - quantity);
    }
}