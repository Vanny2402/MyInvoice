
package com.example.invoicing.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(
        name = "payments",
        indexes = {
                @Index(name = "idx_payment_customer_id", columnList = "customer_id"),
                @Index(name = "idx_payment_sale_id", columnList = "sale_id"),
                @Index(name = "idx_payment_payment_date", columnList = "payment_date")
        }
)
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Customer customer;

    @ManyToOne
    private Sale sale;

    private BigDecimal amount;

    private String remark;

    private LocalDateTime paymentDate = LocalDateTime.now();
}
