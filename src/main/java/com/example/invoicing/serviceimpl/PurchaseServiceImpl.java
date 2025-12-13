package com.example.invoicing.serviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.invoicing.entity.Product;
import com.example.invoicing.entity.Purchase;
import com.example.invoicing.entity.PurchaseItem;
import com.example.invoicing.repository.ProductRepository;
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

    @Override
    public List<Purchase> findAll() {
        return purchaseRepository.findAll();
    }

    @Override
    public Purchase findById(Long id) {
        return purchaseRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Purchase not found"));
    }

    @Override
    public Purchase create(Purchase purchase) {
        BigDecimal total = BigDecimal.ZERO;

        if (purchase.getItems() == null) {
            purchase.setItems(new ArrayList<>());
        }

        for (PurchaseItem item : purchase.getItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

            // Increase stock on purchase
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);

            // Calculate line total
            BigDecimal qty = BigDecimal.valueOf(item.getQuantity());
            BigDecimal lineTotal = item.getPrice().multiply(qty);

            item.setLineTotal(lineTotal);
            item.setProduct(product);
            item.setPurchase(purchase);

            total = total.add(lineTotal);
        }

        purchase.setTotalPrice(total);
        return purchaseRepository.save(purchase);
    }

    @Override
    public Purchase update(Long id, Purchase data) {
        Purchase oldPurchase = findById(id);

        // Revert old stock increases
        for (PurchaseItem oldItem : oldPurchase.getItems()) {
            Product p = oldItem.getProduct();
            p.setStock(p.getStock() - oldItem.getQuantity());
            productRepository.save(p);
        }

        // Apply new items and re-increase stock
        BigDecimal newTotal = BigDecimal.ZERO;
        List<PurchaseItem> newItems = new ArrayList<>();

        for (PurchaseItem newItem : data.getItems()) {
            Product p = productRepository.findById(newItem.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

            p.setStock(p.getStock() + newItem.getQuantity());
            productRepository.save(p);

            BigDecimal qty = BigDecimal.valueOf(newItem.getQuantity());
            BigDecimal lineTotal = newItem.getPrice().multiply(qty);

            newItem.setLineTotal(lineTotal);
            newItem.setPurchase(oldPurchase);
            newItem.setProduct(p);

            newItems.add(newItem);
            newTotal = newTotal.add(lineTotal);
        }

        oldPurchase.setItems(newItems);
        oldPurchase.setRemark(data.getRemark());
        oldPurchase.setSupplier(data.getSupplier());
        oldPurchase.setTotalPrice(newTotal);
        oldPurchase.setCreatedAt(data.getCreatedAt() != null ? data.getCreatedAt() : oldPurchase.getCreatedAt());

        return purchaseRepository.save(oldPurchase);
    }

    @Override
    public void delete(Long id) {
        Purchase purchase = findById(id);

        // Revert stock increases
        for (PurchaseItem item : purchase.getItems()) {
            Product p = item.getProduct();
            p.setStock(p.getStock() - item.getQuantity());
            productRepository.save(p);
        }

        purchaseRepository.delete(purchase);
    }
}
