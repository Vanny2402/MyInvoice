package com.example.invoicing.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.invoicing.entity.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long>{
	List<Sale> findByCustomerId(Long customerId);
	List<Sale> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);	
	
}
