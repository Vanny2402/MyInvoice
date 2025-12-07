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
}
