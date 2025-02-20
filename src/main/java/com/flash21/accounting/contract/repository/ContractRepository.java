package com.flash21.accounting.contract.repository;

import com.flash21.accounting.contract.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {
    @Query("SELECT c FROM Contract c WHERE c.workEndDate BETWEEN :startDate AND :endDate")
    List<Contract> findContractsEndingWithinDates(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
