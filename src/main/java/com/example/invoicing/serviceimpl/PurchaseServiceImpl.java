package com.example.invoicing.serviceimpl;

import java.math.BigDecimal;
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

	@Override
	public List<Purchase> findAll() {
		return purchaseRepository.findAll();
	}

	@Override
	public Purchase findById(Long id) {
		return purchaseRepository.findById(id).orElseThrow(() -> new RuntimeException("Purchase not found"));
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

			// ✅ Update product's purchase price 
			product.setPurchasePrice(item.getPrice().doubleValue());			
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

	    // Clear the existing items list instead of replacing it
	    oldPurchase.getItems().clear();
	    BigDecimal newTotal = BigDecimal.ZERO;
	    for (PurchaseItem newItem : data.getItems()) {
	        Product p = productRepository.findById(newItem.getProduct().getId())
	                .orElseThrow(() -> new RuntimeException("Product not found"));

	        // Increase stock for new items
	        p.setStock(p.getStock() + newItem.getQuantity());
	        productRepository.save(p);

	        BigDecimal qty = BigDecimal.valueOf(newItem.getQuantity());
	        BigDecimal lineTotal = newItem.getPrice().multiply(qty);

	        newItem.setLineTotal(lineTotal);
	        newItem.setPurchase(oldPurchase); // keep same purchase reference
	        newItem.setProduct(p);

	        oldPurchase.getItems().add(newItem); // mutate existing list
	        newTotal = newTotal.add(lineTotal);
	    }

	    oldPurchase.setRemark(data.getRemark());
	    oldPurchase.setSupplier(data.getSupplier());
	    oldPurchase.setTotalPrice(newTotal);
	    oldPurchase.setCreatedAt(data.getCreatedAt() != null ? data.getCreatedAt() : oldPurchase.getCreatedAt());

	    return purchaseRepository.save(oldPurchase);
	}

	@Override
	public void delete(Long id) {
		Purchase purchase = findById(id);
	    for (PurchaseItem item : purchase.getItems()) {
	        Product p = item.getProduct();
	        int newStock = p.getStock() - item.getQuantity();

	        if (newStock >= 0) {
	            p.setStock(newStock);
	            productRepository.save(p);
	        } else {
	            throw new IllegalStateException(
	                "Cannot delete purchase because product '" + p.getName() +
	                "' would have negative stock. Current stock: " + p.getStock() +
	                ", quantity to revert: " + item.getQuantity()
	            );
	        }
	    }
		purchaseRepository.delete(purchase);
	}
	
	
	public PurchaseSummaryDTO findSummaryById(Long id) {
	    Purchase purchase = findById(id);
	    return PurchaseMapper.toSummaryDTO(purchase); 
	}
	
	@Override
	public List<PurchaseSummaryDTO> findAllSummaries() {
	    return findAll().stream()
	        .map(PurchaseMapper::toSummaryDTO)
	        .toList();
	}

	
	@Override
	public List<PurchaseSummaryDTO> findSummariesByMonthYear(int month, int year) {
	    return purchaseRepository.findByMonthAndYear(month, year).stream()
	        .map(PurchaseMapper::toSummaryDTO) // call mapper
	        .toList();
	}
	
	@Override
	public Page<ItemDTO> findItemSummariesByPurchaseId(Long purchaseId, Pageable pageable) {
	    Page<PurchaseItem> page = purchaseItemRepository.findByPurchaseId(purchaseId, pageable);
	    return page.map(PurchaseMapper::toItemDTO);
	}
}
