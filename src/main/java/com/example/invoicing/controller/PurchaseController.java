package com.example.invoicing.controller;

import com.example.invoicing.dto.ItemDTO;
import com.example.invoicing.dto.PurchaseSummaryDTO;
import com.example.invoicing.entity.Purchase;
import com.example.invoicing.service.PurchaseService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchases")
@CrossOrigin
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService service;

    // ===============================
    // Create
    // ===============================
    @PostMapping
    public PurchaseSummaryDTO create(@RequestBody Purchase data) {
        Purchase created = service.create(data);
        return service.findSummaryById(created.getId());
    }

    // ===============================
    // Find All
    // ===============================
    @GetMapping
    public List<PurchaseSummaryDTO> findAll() {
        return service.findAllSummaries();
    }

    // ===============================
    // Find By Id
    // ===============================
    @GetMapping("/{id}")
    public PurchaseSummaryDTO findById(@PathVariable Long id) {
        return service.findSummaryById(id);
    }

    // ===============================
    // Filter By Month / Year
    // Pattern:
    // /api/purchases/filter?month=12&year=2025
    // ===============================
    @GetMapping("/filter")
    public List<PurchaseSummaryDTO> findByMonthYear(
            @RequestParam int month,
            @RequestParam int year
    ) {
        return service.findSummariesByMonthYear(month, year);
    }

    // ===============================
    // Update
    // ===============================
    @PutMapping("/{id}")
    public PurchaseSummaryDTO update(
            @PathVariable Long id,
            @RequestBody Purchase data
    ) {
        Purchase updated = service.update(id, data);
        return service.findSummaryById(updated.getId());
    }

    // ===============================
    // Delete
    // ===============================
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // ===============================
    // Pagination for Items
    // Pattern:
    // /api/purchases/{id}/items?page=0&size=10
    // ===============================
    @GetMapping("/{id}/items")
    public Page<ItemDTO> findItemsByPurchaseId(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        int clampedSize = Math.max(1, Math.min(size, 100));
        return service.findItemSummariesByPurchaseId(
                id,
                PageRequest.of(page, clampedSize)
        );
    }
}
