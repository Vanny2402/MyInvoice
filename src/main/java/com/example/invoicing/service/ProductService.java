package com.example.invoicing.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.invoicing.dto.ProductListDTO;
import com.example.invoicing.entity.Product;

public interface ProductService {

	List<ProductListDTO> findAll();

	Page<ProductListDTO> findPage(Pageable pageable);

	ProductListDTO findById(Long id);
	Product create(Product product);
	Product update(Long id, Product product);
	void delete(Long id);
}
