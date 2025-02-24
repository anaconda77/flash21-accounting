package com.flash21.accounting.detailcontract.domain.repository;

import com.flash21.accounting.detailcontract.domain.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("select p from Payment p where p.detailContract.detailContractId =:detailContractId")
    Optional<Payment> findByDetailContractId(Long detailContractId);
}
