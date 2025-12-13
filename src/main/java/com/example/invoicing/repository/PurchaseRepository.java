package com.example.invoicing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.invoicing.entity.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
}
