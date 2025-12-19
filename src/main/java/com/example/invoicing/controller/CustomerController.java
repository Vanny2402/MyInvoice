package com.example.invoicing.controller;

import com.example.invoicing.entity.Customer;
import com.example.invoicing.entity.Payment;
import com.example.invoicing.entity.Sale;
import com.example.invoicing.service.CustomerService;
import com.example.invoicing.service.PaymentService;
import com.example.invoicing.service.SaleService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private SaleService saleService;

    // -------------------- Customer CRUD --------------------

    @PostMapping
    public ResponseEntity<Customer> create(@RequestBody Customer data) {
        return ResponseEntity.ok(customerService.create(data));
    }

    @GetMapping
    public ResponseEntity<List<Customer>> findAll() {
        return ResponseEntity.ok(customerService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> findById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@PathVariable Long id, @RequestBody Customer data) {
        return ResponseEntity.ok(customerService.update(id, data));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------- New Endpoints --------------------

    // Get all payments by customerId
    @GetMapping("/{id}/payments")
    public ResponseEntity<List<Payment>> findPaymentsByCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.findByCustomerId(id));
    }

    // Get all sales by customerId
    @GetMapping("/{id}/sales")
    public ResponseEntity<List<Sale>> findSalesByCustomer(@PathVariable Long id) {
        return ResponseEntity.ok(saleService.findSaleByCustomerId(id));
    }
}
