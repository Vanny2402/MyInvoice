package com.example.invoicing.service;

import com.example.invoicing.entity.Purchase;
import java.util.List;

public interface PurchaseService {
    Purchase create(Purchase data);
    Purchase update(Long id, Purchase data);
    void delete(Long id);
    Purchase findById(Long id);
    List<Purchase> findAll();
}