package com.example.invoicing.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;

import com.example.invoicing.dto.SaleDTO;
import com.example.invoicing.entity.Sale;

public interface SaleService {

	List<Sale> findAll();
	List<Sale> getSaleCurrentMonth();


	Sale findById(Long id);
	Sale create(Sale sale);

	Sale update(Long id, Sale sale);
	List<SaleDTO> findSaleByCustomerId(Long customerId);
    Page<SaleDTO> findCurrentMonthSales(int page, int size);
	void delete(Long id);
	
	
	Page<SaleDTO> findSalesByDateRange(
	        LocalDate start,
	        LocalDate end,
	        int page,
	        int size
	);

}