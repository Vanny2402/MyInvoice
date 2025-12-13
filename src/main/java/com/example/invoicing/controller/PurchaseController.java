package com.example.invoicing.controller;

import com.example.invoicing.entity.Purchase;
import com.example.invoicing.service.PurchaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin
public class PurchaseController {

    private final PurchaseService service;

    public PurchaseController(PurchaseService service) {
        this.service = service;
    }

    @PostMapping
    public Purchase create(@RequestBody Purchase data) {
        return service.create(data);
    }

    @GetMapping
    public List<Purchase> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Purchase findById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PutMapping("/{id}")
    public Purchase update(@PathVariable Long id, @RequestBody Purchase data) {
        return service.update(id, data);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
