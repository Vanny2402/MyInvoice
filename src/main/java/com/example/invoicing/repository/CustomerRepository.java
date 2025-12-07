package com.example.invoicing.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.invoicing.entity.Customer;
public interface CustomerRepository extends JpaRepository<Customer, Long>{

	
}
