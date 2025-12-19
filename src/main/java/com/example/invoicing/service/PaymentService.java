package com.example.invoicing.service;

import java.util.List;

import com.example.invoicing.dto.PaymentDTO;
import com.example.invoicing.dto.PaymentReportDTO;
import com.example.invoicing.entity.Customer;
import com.example.invoicing.entity.Payment;

public interface PaymentService {

	List<Payment> findAll();
	List<Payment> findByCustomer(Customer customer);
//	List<Payment> findByCustomerId(Long id);

	
	List<PaymentDTO> findByCustomerId(Long customerId);
	Payment findById(Long id);

	Payment create(Payment payment);

	Payment update(Long id, Payment payment);
	

	void delete(Long id);
	List<PaymentReportDTO> getAllPaymentForReport();

}
