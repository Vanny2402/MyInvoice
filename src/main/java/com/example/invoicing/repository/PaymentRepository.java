package com.example.invoicing.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.invoicing.dto.PaymentReportDTO;
import com.example.invoicing.entity.Customer;
import com.example.invoicing.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>{
    @EntityGraph(attributePaths = {"customer"})
    List<Payment> findByCustomerId(Long customerId);
//    List<Payment> findBySaleId(Long saleId);
    List<Payment> findByCustomer(Customer customerId);
    List<Payment> findBySale_Id(Long saleId);

    @Query("""
    	    SELECT new com.example.invoicing.dto.PaymentReportDTO(
    	        p.id,
    	        c.name,
    	        p.amount,
    	        p.remark,
    	        p.paymentDate
    	    )
    	    FROM Payment p
    	    JOIN p.customer c
    	    ORDER BY p.paymentDate DESC
    	""")
    	List<PaymentReportDTO> findAllPaymentReport();



}
