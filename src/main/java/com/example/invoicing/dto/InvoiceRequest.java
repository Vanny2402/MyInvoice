package com.example.invoicing.dto;

import lombok.Data;
import java.util.List;


@Data
public class InvoiceRequest {

    private Long customerId;
    private List<InvoiceItemRequest> items;
}
