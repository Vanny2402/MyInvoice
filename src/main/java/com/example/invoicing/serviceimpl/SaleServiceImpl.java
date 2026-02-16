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

    @Override
    public Page<Sale> getSaleCurrentMonth(Pageable pageable) {

        LocalDate today = LocalDate.now(CAMBODIA_ZONE);
        LocalDate start = today.withDayOfMonth(1);
        LocalDate end = start.plusMonths(1);

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

    /* =====================================================
       UPDATE
    ===================================================== */

    @Override
    public Sale update(Long id, Sale newSale) {

        Sale existing = findById(id);

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
}