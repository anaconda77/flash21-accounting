package com.flash21.accounting.stat.repository;

import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.correspondent.domain.Region;
import com.flash21.accounting.stat.domain.YearStats;
import com.flash21.accounting.stat.domain.YearStatsContent;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StatsRepository extends JpaRepository<YearStats, Long> {

    @Query("select c from Contract c "
        + "left join fetch c.detailContracts "
        + "left join fetch c.correspondent"
        + " where c.admin.id =:userId and extract(YEAR from c.contractStartDate) =:yearNumber")
    List<Contract> getContracts(Long userId, Integer yearNumber);

    @Query("select ys from YearStats ys where ys.userId =:userId")
    List<YearStats> findByUserId(Long userId);

}
