package com.example.invoicing.serviceimpl;

//Complete Details
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.invoicing.dto.SaleDTO;
import com.example.invoicing.dto.SaleDTO.SaleItemDTO;
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
		BigDecimal paid = sale.getPaidAmount() == null ? 
		BigDecimal.ZERO : sale.getPaidAmount();
		sale.setPaidAmount(paid);        // ✅ ADD THIS LINE
		BigDecimal debt = total.subtract(paid);
		sale.setDebt(debt);
		Sale savedSale = saleRepository.save(sale);

		customerService.increaseDebt(savedSale.getCustomer().getId(), debt);

		if (paid.compareTo(BigDecimal.ZERO) > 0) {
			Payment payment = new Payment();
			payment.setCustomer(savedSale.getCustomer());
			payment.setSale(savedSale);
			payment.setAmount(paid);
			payment.setRemark("បង់ជាមួយការទិញ#" + payment.getSale().getId());
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
//		List<Payment> oldPayments = paymentRepository.findByCustomer(oldSale.getCustomer());
//		oldPayments.forEach(paymentRepository::delete);
		List<Payment> oldPayments = paymentRepository.findBySale_Id(oldSale.getId());
		oldPayments.forEach(paymentRepository::delete);


		// Process new items
		BigDecimal newTotal = BigDecimal.ZERO;
		for (SaleItem item : data.getItems()) {
			Product p = productRepository.findById(item.getProduct().getId())
					.orElseThrow(() -> new RuntimeException("Product not found"));

			if (p.getStock() < item.getQty().intValue()) {
				throw new IllegalStateException("Insufficient stock for product: " + p.getName());
			}

			p.setPrice(item.getPrice().doubleValue());

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

	@Override
	public List<SaleDTO> findSaleByCustomerId(Long customerId) {
	    List<Sale> sales = saleRepository.findByCustomerId(customerId);

	    return sales.stream().map(sale -> {
	        SaleDTO dto = new SaleDTO();
	        dto.setId(sale.getId());

	        SaleDTO.CustomerDTO customerDTO = new SaleDTO.CustomerDTO();
	        customerDTO.setId(sale.getCustomer().getId());
	        dto.setCustomer(customerDTO);

	        dto.setTotalPrice(sale.getTotalPrice());
	        dto.setPaidAmount(sale.getPaidAmount());
	        dto.setCreatedAt(sale.getCreatedAt());
	        dto.setRemark(sale.getRemark());

	        List<SaleItemDTO> itemDTOs = new ArrayList<>();
	        if (sale.getItems() != null) {
	            itemDTOs = sale.getItems().stream()
	                .map(item -> {
	                    SaleItemDTO itemDTO = new SaleDTO.SaleItemDTO();
	                    itemDTO.setProductName(item.getProduct().getName());
	                    return itemDTO;
	                })
	                .collect(Collectors.toList());
	        }

	        dto.setItems(itemDTOs);
	        return dto;
	    }).collect(Collectors.toList());
	}

	@Override
	public List<Sale> getSaleCurrentMonth() {
		ZoneId cambodiaZone = ZoneId.of("Asia/Phnom_Penh");		// Start of current month
		LocalDateTime startOfMonth = LocalDate.now(cambodiaZone) .withDayOfMonth(1) .atStartOfDay();		// End of current month (last nanosecond of the month)
		LocalDateTime endOfMonth = startOfMonth.plusMonths(1);
		System.out.println("Start Month: "+startOfMonth + "End of monnt : "+ endOfMonth);
		return saleRepository.findByCreatedAtBetween(startOfMonth, endOfMonth);	
		}
	public Page<SaleDTO> findCurrentMonthSales(int page, int size) {

	    ZoneId zone = ZoneId.of("Asia/Phnom_Penh");
	    LocalDateTime start = LocalDate.now(zone)
	            .withDayOfMonth(1)
	            .atStartOfDay();
	    LocalDateTime end = start.plusMonths(1);

	    Pageable pageable = PageRequest.of(
	    	    page,
	    	    size,
	    	    Sort.by("createdAt").descending()
	    	);

	    // 1️⃣ Page sales (FAST)
	    Page<Sale> salePage =
	            saleRepository.findCurrentMonthSalesPage(start, end, pageable);

	    if (salePage.isEmpty()) {
	        return Page.empty(pageable);
	    }

	    // 2️⃣ Fetch items in ONE query
	    List<Long> saleIds = salePage.getContent()
	            .stream()
	            .map(Sale::getId)
	            .toList();

	    List<Sale> salesWithItems =
	            saleRepository.findSalesWithItems(saleIds);

	    Map<Long, Sale> saleMap = salesWithItems.stream()
	            .collect(Collectors.toMap(Sale::getId, s -> s));

	    // 3️⃣ Map DTO
	    return salePage.map(sale -> {

	        Sale fullSale = saleMap.getOrDefault(
	                sale.getId(),
	                sale
	        );

	        SaleDTO dto = new SaleDTO();
	        dto.setId(fullSale.getId());
	        dto.setCustomer(new SaleDTO.CustomerDTO(
	                fullSale.getCustomer().getId(),
	                fullSale.getCustomer().getName()
	        ));
	        dto.setTotalPrice(fullSale.getTotalPrice());
	        dto.setPaidAmount(
	                fullSale.getPaidAmount() == null
	                        ? BigDecimal.ZERO
	                        : fullSale.getPaidAmount()
	        );
	        dto.setCreatedAt(fullSale.getCreatedAt());
	        dto.setRemark(fullSale.getRemark());

	        dto.setItems(
	            fullSale.getItems() == null
	                ? List.of()
	                : fullSale.getItems().stream()
	                    .map(i -> new SaleDTO.SaleItemDTO(
	                            i.getProduct().getName()
	                    ))
	                    .toList()
	        );

	        return dto;
	    });
	}
	
	@Override
	public Page<SaleDTO> findSalesByDateRange(
	        LocalDate startDate,
	        LocalDate endDate,
	        int page,
	        int size
	) {
	    // ✅ Normalize date range (FIX)
	    LocalDateTime start = startDate.atStartOfDay();
	    LocalDateTime end = endDate.plusDays(1).atStartOfDay();

	    Pageable pageable = PageRequest.of(
	            page,
	            size,
	            Sort.by("createdAt").descending()
	    );

	    Page<Sale> salePage =
	            saleRepository.findSalesByDateRange(start, end, pageable);

	    if (salePage.isEmpty()) {
	        return Page.empty(pageable);
	    }

	    List<Long> saleIds = salePage.getContent()
	            .stream()
	            .map(Sale::getId)
	            .toList();

	    List<Sale> salesWithItems =
	            saleRepository.findSalesWithItems(saleIds);

	    Map<Long, Sale> saleMap = salesWithItems.stream()
	            .collect(Collectors.toMap(Sale::getId, s -> s));

	    return salePage.map(sale -> {
	        Sale fullSale = saleMap.getOrDefault(sale.getId(), sale);

	        SaleDTO dto = new SaleDTO();
	        dto.setId(fullSale.getId());
	        dto.setCustomer(new SaleDTO.CustomerDTO(
	                fullSale.getCustomer().getId(),
	                fullSale.getCustomer().getName()
	        ));
	        dto.setTotalPrice(fullSale.getTotalPrice());
	        dto.setPaidAmount(
	                fullSale.getPaidAmount() == null
	                        ? BigDecimal.ZERO
	                        : fullSale.getPaidAmount()
	        );
	        dto.setCreatedAt(fullSale.getCreatedAt());
	        dto.setRemark(fullSale.getRemark());
	        dto.setItems(
	                fullSale.getItems() == null
	                        ? List.of()
	                        : fullSale.getItems().stream()
	                            .map(i -> new SaleDTO.SaleItemDTO(
	                                    i.getProduct().getName()
	                            ))
	                            .toList()
	        );

	        return dto;
	    });
	}



}
