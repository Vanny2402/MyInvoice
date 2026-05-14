package com.example.invoicing.serviceimpl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.invoicing.dto.ProductListDTO;
import com.example.invoicing.entity.Product;
import com.example.invoicing.repository.ProductRepository;
import com.example.invoicing.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

	private final ProductRepository repo;

	@Override
	public List<ProductListDTO> findAll() {
		return repo.findAllListProjection();
	}

	@Override
	public Page<ProductListDTO> findPage(Pageable pageable) {
		return repo.pageAllListProjection(pageable);
	}

	@Override
	public ProductListDTO findById(Long id) {
		return repo.findListProjectionById(id)
				.orElseThrow(() -> new RuntimeException("Product not found"));
	}

	@Override
	public Product create(Product product) {
		return repo.save(product);
	}

	@Override
	public Product update(Long id, Product data) {
		Product p = repo.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
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
