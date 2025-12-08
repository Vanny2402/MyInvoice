package com.example.invoicing.service;

import com.example.invoicing.entity.Customer;
import java.util.List;
public interface CustomerService {

	List<Customer> findAll();

	Customer findById(Long id);

	Customer create(Customer customer);

	Customer update(Long id, Customer customer);

	void delete(Long id);
}
