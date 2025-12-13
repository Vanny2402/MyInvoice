package com.example.invoicing.service;

import com.example.invoicing.entity.Purchase;
import com.example.invoicing.dto.PurchaseSummaryDTO;

import java.util.List;
import com.example.invoicing.dto.ItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
public interface PurchaseService {
    // Existing entity methods
    Purchase create(Purchase data);
    Purchase update(Long id, Purchase data);
    void delete(Long id);
    Purchase findById(Long id);
    List<Purchase> findAll();
    
    // New DTO methods
    PurchaseSummaryDTO findSummaryById(Long id);
    List<PurchaseSummaryDTO> findAllSummaries();
    List<PurchaseSummaryDTO> findSummariesByMonthYear(int month, int year);
    
    Page<ItemDTO> findItemSummariesByPurchaseId(Long purchaseId, org.springframework.data.domain.Pageable pageable);
    
}
