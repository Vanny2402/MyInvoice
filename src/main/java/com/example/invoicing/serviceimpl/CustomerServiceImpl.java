package com.example.invoicing.serviceimpl;
import org.springframework.data.domain.Sort;
import com.example.invoicing.entity.Customer;
import com.example.invoicing.repository.CustomerRepository;
import com.example.invoicing.service.CustomerService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

	private final CustomerRepository repo;

	@Override
	public List<Customer> findAll() {
//		return repo.findAll();
        return repo.findAll(Sort.by(Sort.Direction.ASC, "name")); 
	}

	@Override
	public Customer findById(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Customer not found"));
	}

	@Override
	public Customer create(Customer customer) {
		return repo.save(customer);
	}

	@Override
	public Customer update(Long id, Customer data) {
		Customer c = findById(id);
		c.setName(data.getName());
		c.setPhone(data.getPhone());
		return repo.save(c);
	}

	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}
}
