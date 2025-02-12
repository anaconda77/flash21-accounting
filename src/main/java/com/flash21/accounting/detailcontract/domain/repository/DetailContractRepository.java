package com.flash21.accounting.detailcontract.domain.repository;

import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetailContractRepository extends JpaRepository<DetailContract, Long> {
    List<DetailContract> findByContractId(Long contractId);
}
