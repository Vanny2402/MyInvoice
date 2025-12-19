package com.example.invoicing.service;

import java.util.List;

import com.example.invoicing.entity.Customer;
import com.example.invoicing.entity.Payment;

public interface PaymentService {

	List<Payment> findAll();
	List<Payment> findByCustomer(Customer customer);
	List<Payment> findByCustomerId(Long id);


	Payment findById(Long id);

	Payment create(Payment payment);

	Payment update(Long id, Payment payment);
	
	

	void delete(Long id);
}
