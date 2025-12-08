package com.example.invoicing.serviceimpl;

import com.example.invoicing.service.SaleService;

import com.example.invoicing.entity.Sale;
import com.example.invoicing.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService{

	 private final SaleRepository repo;

	    @Override
	    public List<Sale> findAll() {
	        return repo.findAll();
	    }

	    @Override
	    public Sale findById(Long id) {
	        return repo.findById(id)
	                .orElseThrow(() -> new RuntimeException("Sale not found"));
	    }

	    @Override
	    public Sale create(Sale sale) {
	        return repo.save(sale);
	    }

	    @Override
	    public Sale update(Long id, Sale data) {
	        Sale s = findById(id);
	        s.setCustomerId(data.getCustomerId());
	        s.setTotalPrice(data.getTotalPrice());
	        s.setPaidAmount(data.getPaidAmount());
	        return repo.save(s);
	    }

	    @Override
	    public void delete(Long id) {
	        repo.deleteById(id);
	    }

}
