package com.example.invoicing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.invoicing.entity.Sale;

public interface SaleRepository extends JpaRepository<Sale, Long>{

}
