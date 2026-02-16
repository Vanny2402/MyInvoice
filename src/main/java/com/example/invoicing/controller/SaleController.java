package com.example.invoicing.controller;

import java.time.LocalDate;
<<<<<<< HEAD
import java.util.List;
=======

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.invoicing.dto.SaleListDTO;
import com.example.invoicing.entity.Sale;
import com.example.invoicing.service.SaleService;

import lombok.RequiredArgsConstructor;
>>>>>>> Sale_listv2

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.invoicing.dto.SaleDTO;
import com.example.invoicing.entity.Sale;
import com.example.invoicing.service.SaleService;
@RestController
@RequestMapping("/api/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService service;

    /* ================= BASIC ================= */

    @PostMapping
    public ResponseEntity<Sale> create(@RequestBody Sale data) {
        return ResponseEntity.ok(service.create(data));
    }

    @GetMapping
    public ResponseEntity<Page<Sale>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.findAll(pageable));
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

    /* ================= DATE RANGE (🔥 FIXED) ================= */

    @GetMapping("/by-date")
    public ResponseEntity<Page<Sale>> getSalesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {

        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        return ResponseEntity.ok(
                service.getSalesByDateRange(start, end, pageable)
        );
    }

    /* ================= CURRENT MONTH ================= */

    @GetMapping("/current-month")
    public ResponseEntity<Page<Sale>> getSaleCurrentMonth(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(service.getSaleCurrentMonth(pageable));
    }

<<<<<<< HEAD
	@GetMapping("/current-month")
	public ResponseEntity<List<Sale>> getSaleCurrentMonth() {
		return ResponseEntity.ok(service.getSaleCurrentMonth());
	}
	
	
	
	@GetMapping("/current-month-dto/month")
	public Page<SaleDTO> getCurrentMonthSales(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "20") int size
	) {
	    return service.findCurrentMonthSales(page, size);
	}

	
	@GetMapping("/by-date")
	public Page<SaleDTO> getSalesByDate(
	        @RequestParam LocalDate startDate,
	        @RequestParam LocalDate endDate,
	        @RequestParam int page,
	        @RequestParam int size
	) {
	    return service.findSalesByDateRange(
	            startDate,
	            endDate,
	            page,
	            size
	    );
	}


=======
    @GetMapping("/current-month-dto")
    public ResponseEntity<java.util.List<SaleListDTO>> getSaleCurrentMonthDTO() {
        return ResponseEntity.ok(service.findCurrentMonthSales());
    }
>>>>>>> Sale_listv2
}
