package com.example.invoicing.serviceimpl;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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
        return purchaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase not found"));
    }

    // =========================
    // CREATE (Batch optimized)
    // =========================

    @Override
    public Purchase create(Purchase purchase) {

        BigDecimal total = BigDecimal.ZERO;
        List<Product> productsToSave = new ArrayList<>();

        for (PurchaseItem item : purchase.getItems()) {

            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            product.setPurchasePrice(item.getPrice().doubleValue());
            product.setStock(product.getStock() + item.getQuantity());

            productsToSave.add(product);

            BigDecimal lineTotal = item.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            item.setLineTotal(lineTotal);
            item.setPurchase(purchase);
            item.setProduct(product);

            total = total.add(lineTotal);
        }

        productRepository.saveAll(productsToSave);

        purchase.setTotalPrice(total);

        return purchaseRepository.save(purchase);
    }

    // =========================
    // UPDATE (Stock safe)
    // =========================

    @Override
    public Purchase update(Long id, Purchase data) {

        Purchase old = findById(id);

        List<Product> productsToSave = new ArrayList<>();

        // revert stock
        for (PurchaseItem oldItem : old.getItems()) {
            Product p = oldItem.getProduct();
            p.setStock(p.getStock() - oldItem.getQuantity());
            productsToSave.add(p);
        }

        old.getItems().clear();

        BigDecimal newTotal = BigDecimal.ZERO;

        for (PurchaseItem newItem : data.getItems()) {

            Product p = productRepository.findById(newItem.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            p.setStock(p.getStock() + newItem.getQuantity());
            productsToSave.add(p);

            BigDecimal lineTotal = newItem.getPrice()
                    .multiply(BigDecimal.valueOf(newItem.getQuantity()));

            newItem.setLineTotal(lineTotal);
            newItem.setPurchase(old);
            newItem.setProduct(p);

            old.getItems().add(newItem);

            newTotal = newTotal.add(lineTotal);
        }

        productRepository.saveAll(productsToSave);

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

        List<Product> productsToSave = new ArrayList<>();

        for (PurchaseItem item : purchase.getItems()) {
            Product p = item.getProduct();

            int newStock = p.getStock() - item.getQuantity();
            if (newStock < 0) {
                throw new IllegalStateException(
                        "Cannot delete purchase because product '"
                                + p.getName() + "' would have negative stock."
                );
            }

            p.setStock(newStock);
            productsToSave.add(p);
        }

        productRepository.saveAll(productsToSave);

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
