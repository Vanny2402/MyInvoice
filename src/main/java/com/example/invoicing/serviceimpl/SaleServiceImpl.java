package com.example.invoicing.serviceimpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
<<<<<<< HEAD
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
=======
import org.springframework.data.domain.Pageable;
>>>>>>> Sale_listv2
import org.springframework.stereotype.Service;

import com.example.invoicing.dto.SaleDTO;
import com.example.invoicing.dto.SaleDTO.SaleItemDTO;
<<<<<<< HEAD
import com.example.invoicing.entity.Customer;
=======
import com.example.invoicing.dto.SaleListDTO;
>>>>>>> Sale_listv2
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

    private static final ZoneId CAMBODIA_ZONE = ZoneId.of("Asia/Phnom_Penh");

    /* =====================================================
       BASIC
    ===================================================== */

    @Override
    public Page<Sale> findAll(Pageable pageable) {
        return saleRepository.findAll(pageable);
    }

    @Override
    public Sale findById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
    }

    /* =====================================================
       DATE FILTER
    ===================================================== */

    @Override
    public Page<Sale> getSalesByDateRange(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable) {

        LocalDateTime start =
                startDate.atStartOfDay(CAMBODIA_ZONE).toLocalDateTime();

        LocalDateTime end =
                endDate.plusDays(1)
                       .atStartOfDay(CAMBODIA_ZONE)
                       .toLocalDateTime();

        return saleRepository.findByDateRange(start, end, pageable);
    }

<<<<<<< HEAD
		sale.setTotalPrice(total);
		BigDecimal paid = sale.getPaidAmount() == null ? 
		BigDecimal.ZERO : sale.getPaidAmount();
		sale.setPaidAmount(paid);        // ✅ ADD THIS LINE
		BigDecimal debt = total.subtract(paid);
		sale.setDebt(debt);
		Sale savedSale = saleRepository.save(sale);
=======
    @Override
    public Page<Sale> getSaleCurrentMonth(Pageable pageable) {

        LocalDate today = LocalDate.now(CAMBODIA_ZONE);
        LocalDate start = today.withDayOfMonth(1);
        LocalDate end = start.plusMonths(1);
>>>>>>> Sale_listv2

        return getSalesByDateRange(start, end.minusDays(1), pageable);
    }

    @Override
    public List<SaleListDTO> findCurrentMonthSales() {

        LocalDate today = LocalDate.now(CAMBODIA_ZONE);
        LocalDate start = today.withDayOfMonth(1);
        LocalDate end = start.plusMonths(1);

        return saleRepository.findCurrentMonthSales(
                start.atStartOfDay(CAMBODIA_ZONE).toLocalDateTime(),
                end.atStartOfDay(CAMBODIA_ZONE).toLocalDateTime()
        );
    }

    /* =====================================================
       CREATE
    ===================================================== */

    @Override
    public Sale create(Sale sale) {

        if (sale.getItems() == null) {
            sale.setItems(new ArrayList<>());
        }

        BigDecimal total = BigDecimal.ZERO;

        for (SaleItem item : sale.getItems()) {

            Product product = getProduct(item.getProduct().getId());

            validateStock(product, item.getQty().intValue());
            decreaseStock(product, item.getQty().intValue());

            BigDecimal lineTotal = item.getPrice().multiply(item.getQty());
            item.setLineTotal(lineTotal);
            item.setProduct(product);
            item.setSale(sale);

            total = total.add(lineTotal);
        }

        sale.setTotalPrice(total);

        BigDecimal paid = safeAmount(sale.getPaidAmount());
        BigDecimal debt = total.subtract(paid);

        sale.setPaidAmount(paid);
        sale.setDebt(debt);

        Sale saved = saleRepository.save(sale);

        customerService.increaseDebt(saved.getCustomer().getId(), debt);

        createPaymentIfNeeded(saved, paid);

        return saved;
    }

<<<<<<< HEAD
		BigDecimal paid = sale.getPaidAmount() == null ? BigDecimal.ZERO : sale.getPaidAmount();
		BigDecimal saleDebt = sale.getTotalPrice().subtract(paid);
		customerService.decreaseDebt(customer.getId(), saleDebt);
		List<Payment> payments = paymentRepository.findBySale_Id(sale.getId());
		payments.forEach(paymentRepository::delete);
=======
    /* =====================================================
       UPDATE
    ===================================================== */

    @Override
    public Sale update(Long id, Sale newSale) {
>>>>>>> Sale_listv2

        Sale existing = findById(id);

<<<<<<< HEAD
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



=======
        // Restore old stock first
        restoreStock(existing);

        // Delete old payments
        paymentRepository.findBySale_Id(existing.getId())
                .forEach(paymentRepository::delete);

        // Recalculate like create
        existing.setItems(newSale.getItems());
        existing.setPaidAmount(newSale.getPaidAmount());
        existing.setRemark(newSale.getRemark());

        return create(existing); // reuse create logic safely
    }

    /* =====================================================
       DELETE
    ===================================================== */

    @Override
    public void delete(Long id) {

        Sale sale = findById(id);

        // Restore stock
        restoreStock(sale);

        // Reverse debt
        BigDecimal paid = safeAmount(sale.getPaidAmount());
        BigDecimal debt = sale.getTotalPrice().subtract(paid);

        customerService.decreaseDebt(
                sale.getCustomer().getId(),
                debt
        );

        // Delete payments
        paymentRepository.findBySale_Id(sale.getId())
                .forEach(paymentRepository::delete);

        saleRepository.delete(sale);
    }

    /* =====================================================
       CUSTOMER SALES
    ===================================================== */

    @Override
    public List<SaleDTO> findSaleByCustomerId(Long customerId) {

        return saleRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    /* =====================================================
       PRIVATE HELPERS
    ===================================================== */

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    private void validateStock(Product product, int qty) {
        if (product.getStock() < qty) {
            throw new IllegalStateException(
                    "Insufficient stock: " + product.getName()
            );
        }
    }

    private void decreaseStock(Product product, int qty) {
        product.setStock(product.getStock() - qty);
    }

    private void increaseStock(Product product, int qty) {
        product.setStock(product.getStock() + qty);
    }

    private void restoreStock(Sale sale) {
        if (sale.getItems() == null) return;

        for (SaleItem item : sale.getItems()) {
            increaseStock(item.getProduct(), item.getQty().intValue());
        }
    }

    private BigDecimal safeAmount(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private void createPaymentIfNeeded(Sale sale, BigDecimal paid) {

        if (paid.compareTo(BigDecimal.ZERO) <= 0) return;

        Payment payment = new Payment();
        payment.setCustomer(sale.getCustomer());
        payment.setSale(sale);
        payment.setAmount(paid);
        payment.setRemark("បង់ជាមួយការទិញ#" + sale.getId());

        paymentRepository.save(payment);
    }

    private SaleDTO mapToDTO(Sale sale) {

        SaleDTO dto = new SaleDTO();
        dto.setId(sale.getId());

        dto.setCustomer(
                new SaleDTO.CustomerDTO(
                        sale.getCustomer().getId(),
                        sale.getCustomer().getName()
                )
        );

        dto.setTotalPrice(sale.getTotalPrice());
        dto.setPaidAmount(sale.getPaidAmount());
        dto.setCreatedAt(sale.getCreatedAt());
        dto.setRemark(sale.getRemark());

        List<SaleItemDTO> items = sale.getItems()
                .stream()
                .map(i -> new SaleItemDTO(
                        i.getProduct().getName()
                ))
                .collect(Collectors.toList());

        dto.setItems(items);

        return dto;
    }
>>>>>>> Sale_listv2
}
