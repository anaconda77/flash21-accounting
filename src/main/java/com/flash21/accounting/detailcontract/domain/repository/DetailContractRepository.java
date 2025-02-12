package com.flash21.accounting.detailcontract.domain.repository;

import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetailContractRepository extends JpaRepository<DetailContract, Long> {
    // Contract 엔티티로 조회
    List<DetailContract> findByContract(Contract contract);
    // 또는 Contract ID로 조회
    List<DetailContract> findByContract_ContractId(Long contractId);
}
