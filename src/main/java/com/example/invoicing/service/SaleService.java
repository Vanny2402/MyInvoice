package com.example.invoicing.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.invoicing.dto.SaleDTO;
import com.example.invoicing.dto.SaleListDTO;
import com.example.invoicing.entity.Sale;

public interface SaleService {

    /* BASIC */
    Page<Sale> findAll(Pageable pageable);
    Sale findById(Long id);

    Sale create(Sale sale);
    Sale update(Long id, Sale sale);
    void delete(Long id);

    List<SaleDTO> findSaleByCustomerId(Long customerId);

    /* DATE FILTER */
    Page<Sale> getSalesByDateRange(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );

    /* MONTH */
    Page<Sale> getSaleCurrentMonth(Pageable pageable);
    List<SaleListDTO> findCurrentMonthSales();
}