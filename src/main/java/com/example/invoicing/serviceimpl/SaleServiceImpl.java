package com.example.invoicing.serviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.invoicing.entity.Customer;
import com.example.invoicing.entity.Product;
import com.example.invoicing.entity.Sale;
import com.example.invoicing.entity.SaleItem;
import com.example.invoicing.repository.ProductRepository;
import com.example.invoicing.repository.SaleRepository;
import com.example.invoicing.service.SaleService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleServiceImpl implements SaleService {

	private final SaleRepository saleRepository;
	private final ProductRepository productRepository;

	@Override
	public List<Sale> findAll() {
		return saleRepository.findAll();
	}

	@Override
	public Sale findById(Long id) {
		return saleRepository.findById(id).orElseThrow(() -> new RuntimeException("Sale not found"));
	}

	@Override
	public Sale create(Sale sale) {

		BigDecimal total = BigDecimal.ZERO;
		
	    if (sale.getItems() == null) {
	        sale.setItems(new ArrayList<>());
	    }
	    for (SaleItem item : sale.getItems()) {
	        item.setSale(sale); // ✅ link back to parent
	    }
	    
	    if (sale.getItems() != null) {
	        for (SaleItem item : sale.getItems()) {
	            Product product = productRepository.findById(item.getProduct().getId())
	                                         .orElseThrow(() -> new RuntimeException("Product not found"));
	            item.setProduct(product); // attach full product entity
	            item.setSale(sale);       // link back to parent
	        }
	    }
	    
		// ✅ Loop through items and adjust stock
		for (SaleItem item : sale.getItems()) {

			Product product = item.getProduct();

			if (product.getStock() < item.getQty().intValue()) {
				throw new IllegalStateException("Insufficient stock for product: " + product.getName());
			}

			// ✅ Reduce stock
			product.setStock(product.getStock() - item.getQty().intValue());
			productRepository.save(product);

			// ✅ Compute line total
			BigDecimal lineTotal = item.getPrice().multiply(item.getQty());
			item.setLineTotal(lineTotal);

			total = total.add(lineTotal);
		}

		sale.setTotalPrice(total);

		// ✅ Debt logic
		Customer customer = sale.getCustomer();
		BigDecimal debt = total.subtract(sale.getPaidAmount());
		customer.setTotalDebt(customer.getTotalDebt().add(debt));

		return saleRepository.save(sale);
		
	}

	@Override
	public Sale update(Long id, Sale data) {

		Sale oldSale = findById(id);

		// Restore stock from old items
		for (SaleItem oldItem : oldSale.getItems()) {
			Product p = oldItem.getProduct();
			p.setStock(p.getStock() + oldItem.getQty().intValue());
			productRepository.save(p);
		}

		// Remove old debt from the old customer
		BigDecimal oldDebt = oldSale.getTotalPrice().subtract(oldSale.getPaidAmount());
		oldSale.getCustomer().setTotalDebt(oldSale.getCustomer().getTotalDebt().subtract(oldDebt));

		// Apply new items
		BigDecimal newTotal = BigDecimal.ZERO;
		for (SaleItem newItem : data.getItems()) {
			Product p = newItem.getProduct();
			if (p.getStock() < newItem.getQty().intValue()) {
				throw new IllegalStateException("Insufficient stock for product: " + p.getName());
			}
			p.setStock(p.getStock() - newItem.getQty().intValue());
			productRepository.save(p);

			BigDecimal lineTotal = newItem.getPrice().multiply(newItem.getQty());
			newItem.setLineTotal(lineTotal);
			newItem.setSale(oldSale); // ✅ ensure bidirectional consistency

			newTotal = newTotal.add(lineTotal);
		}

		// Update sale fields
		oldSale.setItems(data.getItems());
		oldSale.setPaidAmount(data.getPaidAmount());
		oldSale.setCustomer(data.getCustomer());

		// Add new debt to the *new* customer
		BigDecimal newDebt = newTotal.subtract(data.getPaidAmount());
		oldSale.getCustomer().setTotalDebt(oldSale.getCustomer().getTotalDebt().add(newDebt));

		// Let @PreUpdate recompute totalPrice, but keep explicit assignment safe
		oldSale.setTotalPrice(newTotal);

		return saleRepository.save(oldSale);
	}

	@Override
	public void delete(Long id) {

		Sale sale = findById(id);
		Customer customer = sale.getCustomer();

		// ✅ Restore stock
		for (SaleItem item : sale.getItems()) {
			Product p = item.getProduct();
			p.setStock(p.getStock() + item.getQty().intValue());
			productRepository.save(p);
		}

		// ✅ Remove debt
		BigDecimal debt = sale.getTotalPrice().subtract(sale.getPaidAmount());
		customer.setTotalDebt(customer.getTotalDebt().subtract(debt));

		saleRepository.delete(sale);
	}
}
