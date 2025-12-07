package com.example.invoicing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.invoicing.entity.InvoiceItem;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long>{

}
