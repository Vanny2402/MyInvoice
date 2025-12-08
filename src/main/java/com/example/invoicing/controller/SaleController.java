package com.example.invoicing.controller;

import com.example.invoicing.entity.Sale;
import com.example.invoicing.service.SaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

	@Autowired
	private SaleService service;

	@PostMapping
	public ResponseEntity<Sale> create(@RequestBody Sale data) {
		return ResponseEntity.ok(service.create(data));
	}

	@GetMapping
	public ResponseEntity<List<Sale>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Sale> findById(@PathVariable Long id) {
		return ResponseEntity.ok(service.findById(id));
	}

	@PutMapping("/{id}")
	public ResponseEntity<Sale> update(@PathVariable Long id, @RequestBody Sale data) {
		return ResponseEntity.ok(service.update(id, data));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
}
