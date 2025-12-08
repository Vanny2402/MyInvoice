package com.example.invoicing.serviceimpl;


import com.example.invoicing.entity.Payment;
import com.example.invoicing.repository.PaymentRepository;
import com.example.invoicing.service.PaymentService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
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
        return repo.save(payment);
    }

    @Override
    public Payment update(Long id, Payment data) {
        Payment p = findById(id);
        p.setAmount(data.getAmount());
        p.setPaymentDate(data.getPaymentDate());
        return repo.save(p);
    }

    @Override
    public void delete(Long id) {
        repo.deleteById(id);
    }
}
