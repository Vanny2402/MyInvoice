package com.example.invoicing.service;

import com.example.invoicing.entity.Product;
import java.util.List;
public interface ProductService {
	
	List<Product> findAll();
	Product findById(Long id);
	Product create(Product product);
	Product update(Long id, Product product);
	void delete(Long id);
}
