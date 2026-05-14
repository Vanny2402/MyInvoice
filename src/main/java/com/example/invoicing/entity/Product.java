package com.example.invoicing.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
        name = "product",
        indexes = {
                @Index(name = "idx_product_name", columnList = "name")
        }
)
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

    /** Stored in DB but omitted from JSON; list/detail APIs use {@link com.example.invoicing.dto.ProductListDTO}. */
    @JsonIgnore
    @Column(columnDefinition = "bytea")
    private byte[] image;

}
