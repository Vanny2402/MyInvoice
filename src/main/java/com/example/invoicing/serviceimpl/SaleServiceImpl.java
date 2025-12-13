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
        BigDecimal total = BigDecimal.ZERO;

        if (sale.getItems() == null) {
            sale.setItems(new ArrayList<>());
        }

        for (SaleItem item : sale.getItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (product.getStock() < item.getQty().intValue()) {
                throw new IllegalStateException("Insufficient stock for product: " + product.getName());
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

        Customer customer = sale.getCustomer();
        BigDecimal debt = total.subtract(sale.getPaidAmount());
        customerService.increaseDebt(customer.getId(), debt);

        return saleRepository.save(sale);
    }

    @Override
    public Sale update(Long id, Sale data) {
        Sale oldSale = findById(id);

        // restore stock
        for (SaleItem oldItem : oldSale.getItems()) {
            Product p = oldItem.getProduct();
            p.setStock(p.getStock() + oldItem.getQty().intValue());
            productRepository.save(p);
        }

        // remove old debt
        BigDecimal oldDebt = oldSale.getTotalPrice().subtract(oldSale.getPaidAmount());
        customerService.decreaseDebt(oldSale.getCustomer().getId(), oldDebt);

        // apply new items
        BigDecimal newTotal = BigDecimal.ZERO;
        for (SaleItem newItem : data.getItems()) {
            Product p = productRepository.findById(newItem.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            if (p.getStock() < newItem.getQty().intValue()) {
                throw new IllegalStateException("Insufficient stock for product: " + p.getName());
            }

            p.setStock(p.getStock() - newItem.getQty().intValue());
            productRepository.save(p);

            BigDecimal lineTotal = newItem.getPrice().multiply(newItem.getQty());
            newItem.setLineTotal(lineTotal);
            newItem.setSale(oldSale);

            newTotal = newTotal.add(lineTotal);
        }

        oldSale.setItems(data.getItems());
        oldSale.setPaidAmount(data.getPaidAmount());
        oldSale.setCustomer(data.getCustomer());
        oldSale.setTotalPrice(newTotal);

        // add new debt
        BigDecimal newDebt = newTotal.subtract(data.getPaidAmount());
        customerService.increaseDebt(oldSale.getCustomer().getId(), newDebt);

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

        BigDecimal debt = sale.getTotalPrice().subtract(sale.getPaidAmount());
        customerService.decreaseDebt(customer.getId(), debt);

        saleRepository.delete(sale);
    }
}
