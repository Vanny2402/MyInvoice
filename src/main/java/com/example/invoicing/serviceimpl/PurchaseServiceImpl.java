package com.example.invoicing.serviceimpl;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.invoicing.dto.ItemDTO;
import com.example.invoicing.dto.PurchaseSummaryDTO;
import com.example.invoicing.entity.Product;
import com.example.invoicing.entity.Purchase;
import com.example.invoicing.entity.PurchaseItem;
import com.example.invoicing.mapper.PurchaseMapper;
import com.example.invoicing.repository.ProductRepository;
import com.example.invoicing.repository.PurchaseItemRepository;
import com.example.invoicing.repository.PurchaseRepository;
import com.example.invoicing.service.PurchaseService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseServiceImpl implements PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final ProductRepository productRepository;
    private final PurchaseItemRepository purchaseItemRepository;

    // =========================
    // FIND
    // =========================

    @Override
    public List<Purchase> findAll() {
        return purchaseRepository.findAll();
    }

    @Override
    public Purchase findById(Long id) {
        return purchaseRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));
    }

    // =========================
    // CREATE (Batch optimized)
    // =========================

    @Override
    public Purchase create(Purchase purchase) {

        if (purchase.getItems() == null || purchase.getItems().isEmpty()) {
            throw new IllegalArgumentException("Purchase must have at least one line item");
        }

        if (purchase.getCreatedAt() == null) {
            purchase.setCreatedAt(ZonedDateTime.now(ZoneId.of("Asia/Phnom_Penh")));
        }

        BigDecimal total = BigDecimal.ZERO;
        Map<Long, Product> productsToSave = new LinkedHashMap<>();

        for (PurchaseItem item : purchase.getItems()) {

            if (item.getProduct() == null || item.getProduct().getId() == null) {
                throw new IllegalArgumentException("Each line item must reference a product id");
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Each line item must have a positive quantity");
            }
            if (item.getPrice() == null) {
                throw new IllegalArgumentException("Each line item must have a price");
            }

            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            product.setPurchasePrice(item.getPrice().doubleValue());
            int currentStock = Objects.requireNonNullElse(product.getStock(), 0);
            product.setStock(currentStock + item.getQuantity());

            productsToSave.put(product.getId(), product);

            BigDecimal lineTotal = item.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            item.setLineTotal(lineTotal);
            item.setPurchase(purchase);
            item.setProduct(product);

            total = total.add(lineTotal);
        }

        productRepository.saveAll(productsToSave.values());

        purchase.setTotalPrice(total);

        return purchaseRepository.save(purchase);
    }

    // =========================
    // UPDATE (Stock safe)
    // =========================

    @Override
    public Purchase update(Long id, Purchase data) {

        Purchase old = findById(id);

        if (data.getItems() == null || data.getItems().isEmpty()) {
            throw new IllegalArgumentException("Purchase must have at least one line item");
        }

        Map<Long, Product> productsToSave = new LinkedHashMap<>();

        // revert stock from old lines
        for (PurchaseItem oldItem : old.getItems()) {
            Product p = oldItem.getProduct();
            int reverted = Objects.requireNonNullElse(p.getStock(), 0) - oldItem.getQuantity();
            if (reverted < 0) {
                throw new IllegalStateException(
                        "Cannot update purchase: stock for '" + p.getName() + "' would become negative.");
            }
            p.setStock(reverted);
            productsToSave.put(p.getId(), p);
        }

        old.getItems().clear();

        BigDecimal newTotal = BigDecimal.ZERO;

        for (PurchaseItem newItem : data.getItems()) {

            if (newItem.getProduct() == null || newItem.getProduct().getId() == null) {
                throw new IllegalArgumentException("Each line item must reference a product id");
            }
            if (newItem.getQuantity() == null || newItem.getQuantity() <= 0) {
                throw new IllegalArgumentException("Each line item must have a positive quantity");
            }
            if (newItem.getPrice() == null) {
                throw new IllegalArgumentException("Each line item must have a price");
            }

            Product p = productRepository.findById(newItem.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            p.setPurchasePrice(newItem.getPrice().doubleValue());
            int newStock = Objects.requireNonNullElse(p.getStock(), 0) + newItem.getQuantity();
            p.setStock(newStock);
            productsToSave.put(p.getId(), p);

            BigDecimal lineTotal = newItem.getPrice()
                    .multiply(BigDecimal.valueOf(newItem.getQuantity()));

            newItem.setLineTotal(lineTotal);
            newItem.setPurchase(old);
            newItem.setProduct(p);

            old.getItems().add(newItem);

            newTotal = newTotal.add(lineTotal);
        }

        productRepository.saveAll(productsToSave.values());

        old.setSupplier(data.getSupplier());
        old.setRemark(data.getRemark());
        old.setCreatedAt(
                data.getCreatedAt() != null
                        ? data.getCreatedAt()
                        : old.getCreatedAt()
        );
        old.setTotalPrice(newTotal);

        return purchaseRepository.save(old);
    }

    // =========================
    // DELETE
    // =========================

    @Override
    public void delete(Long id) {

        Purchase purchase = findById(id);

        Map<Long, Product> productsToSave = new LinkedHashMap<>();

        for (PurchaseItem item : purchase.getItems()) {
            Product p = item.getProduct();

            int newStock = Objects.requireNonNullElse(p.getStock(), 0) - item.getQuantity();
            if (newStock < 0) {
                throw new IllegalStateException(
                        "Cannot delete purchase because product '"
                                + p.getName() + "' would have negative stock."
                );
            }

            p.setStock(newStock);
            productsToSave.put(p.getId(), p);
        }

        productRepository.saveAll(productsToSave.values());

        purchaseRepository.delete(purchase);
    }

    // =========================
    // DTO Methods
    // =========================

    @Override
    public PurchaseSummaryDTO findSummaryById(Long id) {
        return PurchaseMapper.toSummaryDTO(findById(id));
    }

    @Override
    public List<PurchaseSummaryDTO> findAllSummaries() {
        return purchaseRepository.findAll()
                .stream()
                .map(PurchaseMapper::toSummaryDTO)
                .toList();
    }

    @Override
    public List<PurchaseSummaryDTO> findSummariesByMonthYear(int month, int year) {

        ZonedDateTime start = ZonedDateTime.of(
                year, month, 1, 0, 0, 0, 0,
                ZoneId.systemDefault()
        );

        ZonedDateTime end = start.plusMonths(1).minusNanos(1);

        return purchaseRepository
                .findByDateRangeWithItems(start, end)
                .stream()
                .map(PurchaseMapper::toSummaryDTO)
                .toList();
    }

    @Override
    public Page<ItemDTO> findItemSummariesByPurchaseId(
            Long purchaseId,
            Pageable pageable
    ) {
        return purchaseItemRepository
                .findByPurchaseId(purchaseId, pageable)
                .map(PurchaseMapper::toItemDTO);
    }
}
