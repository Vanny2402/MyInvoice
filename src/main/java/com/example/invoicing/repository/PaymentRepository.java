package com.example.invoicing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.invoicing.entity.Customer;
import com.example.invoicing.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>{
    List<Payment> findByCustomerId(Long customerId);
//    List<Payment> findBySaleId(Long saleId);
    List<Payment> findByCustomer(Customer customer);
    List<Payment> findBySale_Id(Long saleId);

    //    List<Payment> findByInvoiceId(Long invoiceId);

}
