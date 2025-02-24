package com.flash21.accounting.stat.repository;

import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.stat.domain.YearStats;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StatsRepository extends JpaRepository<YearStats, Long> {

    @Query("select c from Contract c left join fetch c.detailContracts"
        + " where c.admin.id =:userId and function('YEAR', c.contractStartDate) =:year")
    List<Contract> getContracts(Long userId, Integer year);

}
