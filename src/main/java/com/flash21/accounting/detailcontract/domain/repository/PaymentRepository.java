package com.flash21.accounting.detailcontract.domain.repository;

import com.flash21.accounting.detailcontract.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
