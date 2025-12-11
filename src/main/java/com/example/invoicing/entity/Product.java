package com.example.invoicing.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String productColor;
    private String productType;
    private String remark;
    private Double price;
    private Double purchasePrice;
    private Integer stock = 0;
    @Column(columnDefinition = "bytea")
    private byte[] image;

}
