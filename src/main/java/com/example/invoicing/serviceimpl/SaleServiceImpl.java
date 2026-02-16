package com.example.invoicing.serviceimpl;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.invoicing.dto.SaleDTO;
import com.example.invoicing.dto.SaleDTO.SaleItemDTO;
import com.example.invoicing.dto.SaleListDTO;
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

    /* ================= BASIC ================= */

    @Override
    public Page<Sale> findAll(Pageable pageable) {
        return saleRepository.findAll(pageable);
    }

    @Override
    public Sale findById(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Sale not found"));
    }

    /* ================= DATE FILTER ================= */

    @Override
    public Page<Sale> getSalesByDateRange(
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {

        LocalDateTime start =
                startDate.atStartOfDay(CAMBODIA_ZONE).toLocalDateTime();

        LocalDateTime end =
                endDate.plusDays(1)
                       .atStartOfDay(CAMBODIA_ZONE)
                       .toLocalDateTime();

        return saleRepository.findByDateRange(start, end, pageable);
    }

    /* ================= CURRENT MONTH ================= */

    @Override
    public Page<Sale> getSaleCurrentMonth(Pageable pageable) {

        LocalDate today = LocalDate.now(CAMBODIA_ZONE);

        LocalDate start = today.withDayOfMonth(1);
        LocalDate end = start.plusMonths(1).minusDays(1);

        return getSalesByDateRange(start, end, pageable);
    }

    @Override
    public List<SaleListDTO> findCurrentMonthSales() {

        LocalDate today = LocalDate.now(CAMBODIA_ZONE);
        LocalDate start = today.withDayOfMonth(1);
        LocalDate end = start.plusMonths(1);

        LocalDateTime startTime =
                start.atStartOfDay(CAMBODIA_ZONE).toLocalDateTime();
        LocalDateTime endTime =
                end.atStartOfDay(CAMBODIA_ZONE).toLocalDateTime();

        return saleRepository.findCurrentMonthSales(startTime, endTime);
    }

    /* ================= CREATE ================= */

    @Override
    public Sale create(Sale sale) {

        if (sale.getItems() == null) {
            sale.setItems(new ArrayList<>());
        }

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

        BigDecimal paid = sale.getPaidAmount() == null
                ? BigDecimal.ZERO
                : sale.getPaidAmount();

        BigDecimal debt = total.subtract(paid);
        sale.setDebt(debt);

        Sale savedSale = saleRepository.save(sale);

        customerService.increaseDebt(savedSale.getCustomer().getId(), debt);

        if (paid.compareTo(BigDecimal.ZERO) > 0) {
            Payment payment = new Payment();
            payment.setCustomer(savedSale.getCustomer());
            payment.setSale(savedSale);
            payment.setAmount(paid);
            payment.setRemark("បង់ជាមួយការទិញ#" + savedSale.getId());
            paymentRepository.save(payment);
        }

        return savedSale;
    }

    /* ================= UPDATE / DELETE ================= */

    @Override
    public Sale update(Long id, Sale data) {
        return saleRepository.save(data);
    }

    @Override
    public void delete(Long id) {
        saleRepository.delete(findById(id));
    }

    /* ================= CUSTOMER SALES ================= */

    @Override
    public List<SaleDTO> findSaleByCustomerId(Long customerId) {

        return saleRepository.findByCustomerId(customerId)
                .stream()
                .map(sale -> {
                    SaleDTO dto = new SaleDTO();
                    dto.setId(sale.getId());

                    SaleDTO.CustomerDTO customerDTO = new SaleDTO.CustomerDTO();
                    customerDTO.setId(sale.getCustomer().getId());
                    dto.setCustomer(customerDTO);

                    dto.setTotalPrice(sale.getTotalPrice());
                    dto.setPaidAmount(sale.getPaidAmount());
                    dto.setCreatedAt(sale.getCreatedAt());
                    dto.setRemark(sale.getRemark());

                    List<SaleItemDTO> items = sale.getItems()
                            .stream()
                            .map(i -> {
                                SaleItemDTO itemDTO = new SaleItemDTO();
                                itemDTO.setProductName(i.getProduct().getName());
                                return itemDTO;
                            })
                            .collect(Collectors.toList());

                    dto.setItems(items);
                    return dto;
                })
                .collect(Collectors.toList());
    }
}