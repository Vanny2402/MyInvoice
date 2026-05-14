package com.example.invoicing.controller;

import com.example.invoicing.dto.ProductListDTO;
import com.example.invoicing.entity.Product;
import com.example.invoicing.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
		return ResponseEntity.ok(service.create(data));
	}

	@GetMapping
	public ResponseEntity<List<ProductListDTO>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping("/paged")
	public ResponseEntity<Page<ProductListDTO>> findAllPaged(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "50") int size,
			@RequestParam(defaultValue = "name") String sortBy,
			@RequestParam(defaultValue = "asc") String direction) {
		int safeSize = Math.min(Math.max(size, 1), 500);
		Sort sort = direction.equalsIgnoreCase("desc")
				? Sort.by(resolveProductSortProperty(sortBy)).descending()
				: Sort.by(resolveProductSortProperty(sortBy)).ascending();
		Pageable pageable = PageRequest.of(page, safeSize, sort);
		return ResponseEntity.ok(service.findPage(pageable));
	}

	@GetMapping("/{id}")
	public ResponseEntity<ProductListDTO> findById(@PathVariable Long id) {
		return ResponseEntity.ok(service.findById(id));
	}

	private static String resolveProductSortProperty(String sortBy) {
		if (sortBy == null || sortBy.isBlank()) {
			return "name";
		}
		return switch (sortBy) {
			case "id", "name", "stock", "price", "purchasePrice", "productType", "productColor", "remark" -> sortBy;
			default -> "name";
		};
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
