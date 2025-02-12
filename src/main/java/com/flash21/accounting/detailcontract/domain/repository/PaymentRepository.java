package com.flash21.accounting.detailcontract.domain.repository;

import com.flash21.accounting.detailcontract.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
