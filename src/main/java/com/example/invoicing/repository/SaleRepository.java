package com.example.invoicing.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.invoicing.dto.SaleListDTO;
import com.example.invoicing.entity.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long>{
	List<Sale> findByCustomerId(Long customerId);
	List<Sale> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);	
	
	@Query("""
			SELECT new com.example.invoicing.dto.SaleListDTO(
			    s.id,
			    c.id,
			    c.name,
			    s.totalPrice,
			    s.createdAt
			)
			FROM Sale s
			JOIN s.customer c
			WHERE MONTH(s.createdAt) = MONTH(CURRENT_DATE)
			AND YEAR(s.createdAt) = YEAR(CURRENT_DATE)
			ORDER BY s.createdAt DESC
			""")
	List<SaleListDTO> findCurrentMonthSales();
}
