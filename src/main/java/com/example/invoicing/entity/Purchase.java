package com.example.invoicing.entity;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "purchase")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Purchase {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToMany(mappedBy = "purchase", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<PurchaseItem> items = new ArrayList<>();

	private BigDecimal totalPrice;
	private String remark;
	private String supplier;
	private ZonedDateTime createdAt = ZonedDateTime.now();

	// Remove @PrePersist; stock updates now happen in service
}
