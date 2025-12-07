package com.example.invoicing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.invoicing.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>{

    List<Payment> findByInvoiceId(Long invoiceId);

}
