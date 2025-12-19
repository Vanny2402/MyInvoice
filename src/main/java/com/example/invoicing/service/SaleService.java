package com.example.invoicing.service;

import com.example.invoicing.dto.SaleDTO;
import com.example.invoicing.dto.SaleListDTO;
import com.example.invoicing.entity.Sale;
import java.util.List;

public interface SaleService {

	List<Sale> findAll();
	List<Sale> getSaleCurrentMonth();


	Sale findById(Long id);
	Sale create(Sale sale);

	Sale update(Long id, Sale sale);
	List<SaleDTO> findSaleByCustomerId(Long customerId);
	List<SaleListDTO> findCurrentMonthSales();
	void delete(Long id);
}
