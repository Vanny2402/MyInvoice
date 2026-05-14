package com.example.invoicing.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(
        name = "customers",
        indexes = {
                @Index(name = "idx_customer_name", columnList = "name")
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;
	private String phone;
	private String address;
	
    @Column(name = "total_debt", precision = 14, scale = 2)
    private BigDecimal totalDebt = BigDecimal.ZERO;
}

