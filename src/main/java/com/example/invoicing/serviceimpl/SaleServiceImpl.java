package com.example.invoicing.serviceimpl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.invoicing.entity.Product;
import com.example.invoicing.entity.Sale;
import com.example.invoicing.repository.ProductRepository;
import com.example.invoicing.repository.SaleRepository;
import com.example.invoicing.service.SaleService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleServiceImpl implements SaleService{

	 private final SaleRepository repo;
	 private final ProductRepository productRepository; // NEW

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
	        Product product = sale.getProduct(); // now directly linked

	        // Check stock availability
	        if (product.getStock() < sale.getQty().intValue()) {
	            throw new IllegalStateException("Insufficient stock for product: " + product.getName());
	        }

	        // Decrease stock
	        product.setStock(product.getStock() - sale.getQty().intValue());
	        productRepository.save(product);

	        // Save sale
	        return repo.save(sale);
	    }

	    @Override
	    public Sale update(Long id, Sale data) {
	        Sale existingSale = findById(id);
	        Product oldProduct = existingSale.getProduct();
	        int oldQty = existingSale.getQty().intValue();

	        Product newProduct = data.getProduct();
	        int newQty = data.getQty().intValue();

	        // If product changed → restore old stock
	        if (!oldProduct.getId().equals(newProduct.getId())) {
	            oldProduct.setStock(oldProduct.getStock() + oldQty);
	            productRepository.save(oldProduct);

	            // Decrease stock for new product
	            if (newProduct.getStock() < newQty) {
	                throw new IllegalStateException("Insufficient stock for product: " + newProduct.getName());
	            }
	            newProduct.setStock(newProduct.getStock() - newQty);
	            productRepository.save(newProduct);

	            existingSale.setProduct(newProduct);
	            existingSale.setQty(data.getQty());
	        } else {
	            // Same product → adjust stock difference
	            int diff = newQty - oldQty;
	            if (diff > 0) {
	                if (newProduct.getStock() < diff) {
	                    throw new IllegalStateException("Insufficient stock for product: " + newProduct.getName());
	                }
	                newProduct.setStock(newProduct.getStock() - diff);
	            } else if (diff < 0) {
	                newProduct.setStock(newProduct.getStock() + Math.abs(diff));
	            }
	            productRepository.save(newProduct);

	            existingSale.setQty(data.getQty());
	        }

	        // Update other fields
	        existingSale.setCustomer(data.getCustomer());
	        existingSale.setPrice(data.getPrice());
	        existingSale.setTotalPrice(data.getTotalPrice());
	        existingSale.setPaidAmount(data.getPaidAmount());

	        return repo.save(existingSale);
	    }

	    @Override
	    public void delete(Long id) {
	        repo.deleteById(id);
	    }

}
