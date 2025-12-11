package com.example.invoicing.controller;

import com.example.invoicing.entity.Product;
import com.example.invoicing.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

	@Autowired
	private ProductService service;

	@PostMapping
	public ResponseEntity<Product> create(@RequestBody Product data) {
		System.out.println("------------------------------------Debug--------------------------------------------");
	    System.out.println(data); 
		return ResponseEntity.ok(service.create(data));
	}

	@GetMapping
	public ResponseEntity<List<Product>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Product> findById(@PathVariable Long id) {
		return ResponseEntity.ok(service.findById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product data) {
		return ResponseEntity.ok(service.update(id, data));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}

}
