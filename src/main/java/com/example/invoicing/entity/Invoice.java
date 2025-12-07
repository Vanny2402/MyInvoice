package com.example.invoicing.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.*;

@Entity
@Table(name = "invoice")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne
	    private Customer customer;

	    private Double totalAmount;
	    private String status; // UNPAID / PARTIAL / PAID

	    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
	    private List<InvoiceItem> items = new ArrayList<>();
}
