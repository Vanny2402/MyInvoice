package com.example.invoicing.serviceimpl;

import com.example.invoicing.entity.Customer;
import com.example.invoicing.entity.Payment;
import com.example.invoicing.repository.PaymentRepository;
import com.example.invoicing.service.PaymentService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository repo;

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
        // ✅ Reduce customer debt when new payment is made
        Customer customer = payment.getCustomer();
        if (customer != null) {
            BigDecimal currentDebt = customer.getTotalDebt() != null ? customer.getTotalDebt() : BigDecimal.ZERO;
            customer.setTotalDebt(currentDebt.subtract(payment.getAmount()));
        }
        return repo.save(payment);
    }

    @Override
    public Payment update(Long id, Payment data) {
        Payment existing = findById(id);
        Customer customer = existing.getCustomer();

        if (customer != null) {
            BigDecimal currentDebt = customer.getTotalDebt() != null ? customer.getTotalDebt() : BigDecimal.ZERO;

            // ✅ Restore old amount back to debt
            currentDebt = currentDebt.add(existing.getAmount());

            // ✅ Subtract new amount
            currentDebt = currentDebt.subtract(data.getAmount());

            customer.setTotalDebt(currentDebt);
        }

        existing.setAmount(data.getAmount());
        existing.setPaymentDate(data.getPaymentDate());
        return repo.save(existing);
    }

    @Override
    public void delete(Long id) {
        Payment existing = findById(id);
        Customer customer = existing.getCustomer();

        if (customer != null) {
            BigDecimal currentDebt = customer.getTotalDebt() != null ? customer.getTotalDebt() : BigDecimal.ZERO;
            // ✅ Removing payment means debt increases again
            customer.setTotalDebt(currentDebt.add(existing.getAmount()));
        }

        repo.delete(existing);
    }
}
