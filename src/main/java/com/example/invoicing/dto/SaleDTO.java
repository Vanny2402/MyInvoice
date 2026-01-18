package com.example.invoicing.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor // generates SaleDTO()
public class SaleDTO {
	private Long id;
	private CustomerDTO customer;
	private BigDecimal totalPrice;
	private BigDecimal paidAmount;
	private LocalDateTime createdAt;
	private String remark;
	private List<SaleItemDTO> items;

	// Constructor for JPQL query
	public SaleDTO(Long id, Long customerId, String customerName, BigDecimal totalPrice, BigDecimal paidAmount,
			LocalDateTime createdAt, String remark) {
		this.id = id;
		this.customer = new CustomerDTO(customerId, customerName);
		this.totalPrice = totalPrice;
		this.paidAmount = paidAmount;
		this.createdAt = createdAt;
		this.remark = remark;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CustomerDTO {
		private Long id;
		private String name;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class SaleItemDTO {
		private String productName;
	}
}
