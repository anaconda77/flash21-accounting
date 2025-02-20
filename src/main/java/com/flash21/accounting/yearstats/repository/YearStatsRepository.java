package com.flash21.accounting.yearstats.repository;

import com.flash21.accounting.yearstats.domain.YearStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface YearStatsRepository extends JpaRepository<YearStats, Integer> {

    // 연도와 카테고리로 데이터 조회
    List<YearStats> findByYearAndCategory(Integer year, String category);

    // 연도만 입력되면 카테고리 상관없이 전체 데이터 조회
    List<YearStats> findByYear(Integer year);
}
