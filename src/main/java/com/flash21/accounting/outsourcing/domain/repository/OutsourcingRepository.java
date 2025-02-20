package com.flash21.accounting.outsourcing.domain.repository;

import com.flash21.accounting.outsourcing.domain.entity.Outsourcing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OutsourcingRepository extends JpaRepository<Outsourcing, Long> {
    // DetailContract ID로 외주계약 조회
    Optional<Outsourcing> findByDetailContractDetailContractId(Long detailContractId);
}
