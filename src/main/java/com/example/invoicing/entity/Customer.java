package com.example.invoicing.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "customers")
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


