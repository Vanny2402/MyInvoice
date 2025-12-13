package com.example.invoicing.repository;

import com.example.invoicing.entity.PurchaseItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PurchaseItemRepository extends JpaRepository <PurchaseItem, Long> {

    Page<PurchaseItem> findByPurchaseId(Long purchaseId, Pageable pageable);
	
	
}
