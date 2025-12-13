package com.example.invoicing.mapper;

import com.example.invoicing.dto.ItemDTO;
import com.example.invoicing.dto.PurchaseSummaryDTO;
import com.example.invoicing.entity.Purchase;
import com.example.invoicing.entity.PurchaseItem;

import java.util.List;
import java.util.stream.Collectors;

public class PurchaseMapper {

    public static PurchaseSummaryDTO toSummaryDTO(Purchase purchase) {
        List<ItemDTO> items = purchase.getItems().stream()
            .map(PurchaseMapper::toItemDTO)
            .collect(Collectors.toList());

        return new PurchaseSummaryDTO(
            purchase.getId(),
            purchase.getSupplier(),
            purchase.getCreatedAt(),
            purchase.getTotalPrice(),
            items
        );
    }

    public static ItemDTO toItemDTO(PurchaseItem item) {
        return new ItemDTO(
            item.getProduct().getName(),
            item.getQuantity(),
            item.getPrice(),
            item.getLineTotal()
        );
    }
}