package com.flash21.accounting.detailcontract.domain.repository;

import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailContractRepository extends JpaRepository<DetailContract, Long> {
    // Contract ID로 조회
    List<DetailContract> findByContractContractId(Long contractId);
    List<DetailContract> findByContract(Contract contract);
}
