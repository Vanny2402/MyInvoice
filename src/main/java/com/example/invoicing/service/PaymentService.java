package com.example.invoicing.service;

import java.util.List;

import com.example.invoicing.entity.Payment;

public interface PaymentService {

	List<Payment> findAll();

	Payment findById(Long id);

	Payment create(Payment payment);

	Payment update(Long id, Payment payment);

	void delete(Long id);
}
