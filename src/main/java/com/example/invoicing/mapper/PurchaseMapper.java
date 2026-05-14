package com.example.invoicing.mapper;

import com.example.invoicing.dto.ItemDTO;
import com.example.invoicing.dto.PurchaseSummaryDTO;
import com.example.invoicing.entity.Purchase;
import com.example.invoicing.entity.PurchaseItem;

import java.util.List;
import java.util.stream.Collectors;

public class PurchaseMapper {

    private PurchaseMapper() {}

    public static PurchaseSummaryDTO toSummaryDTO(Purchase purchase) {

        List<ItemDTO> items = purchase.getItems() == null
                ? List.of()
                : purchase.getItems()
                    .stream()
                    .map(PurchaseMapper::toItemDTO)
                    .toList();

        return new PurchaseSummaryDTO(
                purchase.getId(),
                purchase.getSupplier(),
                purchase.getCreatedAt(),
                purchase.getTotalPrice(),
                items
        );
    }

    public static ItemDTO toItemDTO(PurchaseItem item) {
        String productName = item.getProduct() != null ? item.getProduct().getName() : "";
        return new ItemDTO(
                productName,
                item.getQuantity(),
                item.getPrice(),
                item.getLineTotal()
        );
    }
}
