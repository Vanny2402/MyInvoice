package com.example.invoicing.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity
@Table(name = "sales")
@Data
public class Sale {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "customer_id")
	private Long customerId;

	private String product;
	private BigDecimal qty = BigDecimal.ZERO;
	private BigDecimal price = BigDecimal.ZERO;

	@Column(name = "total_price", precision = 14, scale = 2)
	private BigDecimal totalPrice;

	@Column(name = "paid_amount", precision = 12, scale = 2)
	private BigDecimal paidAmount = BigDecimal.ZERO;

	private LocalDateTime createdAt = LocalDateTime.now();

	@PrePersist
	@PreUpdate
	public void computeTotal() {
		if (qty == null)
			qty = BigDecimal.ZERO;
		if (price == null)
			price = BigDecimal.ZERO;
		this.totalPrice = qty.multiply(price);
	}

}
