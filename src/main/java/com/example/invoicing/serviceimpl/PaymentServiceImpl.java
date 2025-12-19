package com.example.invoicing.serviceimpl;

import com.example.invoicing.entity.Customer;
import com.example.invoicing.entity.Payment;
import com.example.invoicing.repository.PaymentRepository;
import com.example.invoicing.service.CustomerService;
import com.example.invoicing.service.PaymentService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repo;
    private final CustomerService customerService;

    @Override
    public List<Payment> findAll() {
        return repo.findAll();
    }

    @Override
    public Payment findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    @Override
    public Payment create(Payment payment) {
        Customer customer = payment.getCustomer();
        if (customer != null) {
            customerService.decreaseDebt(customer.getId(), payment.getAmount());
        }
        return repo.save(payment);
    }

    @Override
    public Payment update(Long id, Payment data) {
        Payment existing = findById(id);
        Customer customer = existing.getCustomer();

        if (customer != null) {
            customerService.increaseDebt(customer.getId(), existing.getAmount()); // restore old
            customerService.decreaseDebt(customer.getId(), data.getAmount());     // apply new
        }

        existing.setAmount(data.getAmount());
        existing.setRemark(data.getRemark());
        existing.setPaymentDate(data.getPaymentDate());
        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        Payment existing = findById(id);
        Customer customer = existing.getCustomer();

        if (customer != null) {
            customerService.increaseDebt(customer.getId(), existing.getAmount());
        }

        repo.delete(existing);
    }

    @Override
    public List<Payment> findByCustomer(Customer customer) {
        return repo.findByCustomer(customer);
    }

    @Override
    public List<Payment> findByCustomerId(Long customerId) {
        return repo.findByCustomerId(customerId);
    }

}
