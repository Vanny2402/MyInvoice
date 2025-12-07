package com.example.invoicing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.invoicing.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
