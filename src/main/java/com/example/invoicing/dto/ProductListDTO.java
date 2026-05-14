package com.example.invoicing.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * List/detail view without {@code image} — avoids loading {@code bytea} from the DB for these queries.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductListDTO {

    private Long id;
    private String name;
    private String productColor;
    private String productType;
    private String remark;
    private Double price;
    private Double purchasePrice;
    private Integer stock;
}
