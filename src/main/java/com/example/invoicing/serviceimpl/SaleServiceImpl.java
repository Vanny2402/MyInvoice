package com.example.invoicing.serviceimpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.invoicing.entity.Customer;
import com.example.invoicing.entity.Payment;
import com.example.invoicing.entity.Product;
import com.example.invoicing.entity.Sale;
import com.example.invoicing.entity.SaleItem;
import com.example.invoicing.repository.PaymentRepository;
import com.example.invoicing.repository.ProductRepository;
import com.example.invoicing.repository.SaleRepository;
import com.example.invoicing.service.CustomerService;
import com.example.invoicing.service.SaleService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SaleServiceImpl implements SaleService {

	private final SaleRepository saleRepository;
	private final ProductRepository productRepository;
	private final PaymentRepository paymentRepository;
	private final CustomerService customerService;

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

		if (sale.getItems() == null)
			sale.setItems(new ArrayList<>());

		BigDecimal total = BigDecimal.ZERO;

		for (SaleItem item : sale.getItems()) {
			Product product = productRepository.findById(item.getProduct().getId())
					.orElseThrow(() -> new RuntimeException("Product not found"));

			if (product.getStock() < item.getQty().intValue()) {
				throw new IllegalStateException("Insufficient stock: " + product.getName());
			}

			product.setStock(product.getStock() - item.getQty().intValue());
			productRepository.save(product);

			BigDecimal lineTotal = item.getPrice().multiply(item.getQty());
			item.setLineTotal(lineTotal);
			item.setProduct(product);
			item.setSale(sale);

			total = total.add(lineTotal);
		}

		sale.setTotalPrice(total);

		BigDecimal paid = sale.getPaidAmount() == null ? BigDecimal.ZERO : sale.getPaidAmount();
		BigDecimal debt = total.subtract(paid);
		sale.setDebt(debt);

		Sale savedSale = saleRepository.save(sale);

		if (debt.compareTo(BigDecimal.ZERO) > 0) {
			customerService.increaseDebt(savedSale.getCustomer().getId(), debt);
		}

		if (paid.compareTo(BigDecimal.ZERO) > 0) {
			Payment payment = new Payment();
			payment.setCustomer(savedSale.getCustomer());
			payment.setSale(savedSale);
			payment.setAmount(paid);
			payment.setRemark("Paid at sale");
			paymentRepository.save(payment);
		}

		return savedSale;
	}

	@Override
	public Sale update(Long id, Sale data) {
		Sale oldSale = findById(id);

		// Restore stock
		for (SaleItem item : oldSale.getItems()) {
			Product p = item.getProduct();
			p.setStock(p.getStock() + item.getQty().intValue());
			productRepository.save(p);
		}

		// Remove old debt
		BigDecimal oldDebt = oldSale.getTotalPrice().subtract(oldSale.getPaidAmount());
		customerService.decreaseDebt(oldSale.getCustomer().getId(), oldDebt);

		// Remove old payments
		List<Payment> oldPayments = paymentRepository.findByCustomer(oldSale.getCustomer());
		oldPayments.forEach(paymentRepository::delete);

		// Process new items
		BigDecimal newTotal = BigDecimal.ZERO;
		for (SaleItem item : data.getItems()) {
			Product p = productRepository.findById(item.getProduct().getId())
					.orElseThrow(() -> new RuntimeException("Product not found"));

			if (p.getStock() < item.getQty().intValue()) {
				throw new IllegalStateException("Insufficient stock for product: " + p.getName());
			}

			p.setStock(p.getStock() - item.getQty().intValue());
			productRepository.save(p);

			BigDecimal lineTotal = item.getPrice().multiply(item.getQty());
			item.setLineTotal(lineTotal);
			item.setSale(oldSale);

			newTotal = newTotal.add(lineTotal);
		}

		oldSale.setItems(data.getItems());
		oldSale.setPaidAmount(data.getPaidAmount());
		oldSale.setCustomer(data.getCustomer());
		oldSale.setTotalPrice(newTotal);

		BigDecimal newDebt = newTotal.subtract(data.getPaidAmount());
		if (newDebt.compareTo(BigDecimal.ZERO) > 0) {
			customerService.increaseDebt(oldSale.getCustomer().getId(), newDebt);
		}

		// Auto-create payment for new paid amount
		if (data.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
			Payment payment = new Payment();
			payment.setCustomer(oldSale.getCustomer());
			payment.setAmount(data.getPaidAmount());
			payment.setRemark("Paid at sale update");
			paymentRepository.save(payment);
		}

		return saleRepository.save(oldSale);
	}

	@Override
	public void delete(Long id) {

		Sale sale = findById(id);
		Customer customer = sale.getCustomer();

		for (SaleItem item : sale.getItems()) {
			Product p = item.getProduct();
			p.setStock(p.getStock() + item.getQty().intValue());
			productRepository.save(p);
		}

		BigDecimal paid = sale.getPaidAmount() == null ? BigDecimal.ZERO : sale.getPaidAmount();
		BigDecimal saleDebt = sale.getTotalPrice().subtract(paid);
		customerService.decreaseDebt(customer.getId(), saleDebt);

		List<Payment> payments = paymentRepository.findBySale_Id(sale.getId());
		payments.forEach(paymentRepository::delete);

		saleRepository.delete(sale);
	}
}
