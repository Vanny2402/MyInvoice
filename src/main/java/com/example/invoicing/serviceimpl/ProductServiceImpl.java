package com.example.invoicing.serviceimpl;

import com.example.invoicing.entity.Product;
import com.example.invoicing.repository.ProductRepository;
import com.example.invoicing.service.ProductService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository repo;

	@Override
	public List<Product> findAll() {
		return repo.findAll();
	}

	@Override
	public Product findById(Long id) {
		return repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
	}

	@Override
	public Product create(Product product) {
		return repo.save(product);
	}

	@Override
	public Product update(Long id, Product data) {
		Product p = findById(id);
	    p.setName(data.getName());
	    p.setPrice(data.getPrice());
	    p.setProductColor(data.getProductColor());
	    p.setProductType(data.getProductType());
	    p.setStock(data.getStock());
	    p.setRemark(data.getRemark());
	    p.setPurchasePrice(data.getPurchasePrice());
		return repo.save(p);
	}

	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}
}
