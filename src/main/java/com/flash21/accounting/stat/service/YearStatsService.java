package com.flash21.accounting.stat.service;

import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.correspondent.domain.CorrespondentCategory;
import com.flash21.accounting.correspondent.domain.Region;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.stat.domain.YearStats;
import com.flash21.accounting.stat.domain.YearStatsContent;
import com.flash21.accounting.stat.dto.response.YearStatsResponseDto;
import com.flash21.accounting.stat.repository.StatsRepository;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class YearStatsService {

    private final StatsRepository statsRepository;

    @Transactional
    public YearStatsResponseDto getYearStatistics(Long userId, CorrespondentCategory category,
        Integer year) {
        YearStats yearStats = statsRepository.findByUserId(userId).stream()
            .filter(ys -> Objects.equals(ys.getYearNumber(), year) && ys.getCategory() == category)
            .findFirst()
            .orElse(
                calculateYearStatistics(userId, category, year)); // db에 해당 통계 데이터가 없으면 계산 및 생성하여 리턴

        return YearStatsResponseDto.of(yearStats);
    }

    @Transactional
    public YearStatsResponseDto createYearStatistics(Long userId, CorrespondentCategory category,
        Integer year) {
        return YearStatsResponseDto.of(calculateYearStatistics(userId, category, year));
    }

    @Transactional
    protected YearStats calculateYearStatistics(Long userId, CorrespondentCategory category,
                                                Integer year) {
        List<Contract> returns = statsRepository.getContracts(userId, year);
        Map<String, YearStatsContent> yearStatsContentMap = new HashMap<>();
        Arrays.stream(Region.values())
            .forEach(region -> yearStatsContentMap.put(region.toString(),
                new YearStatsContent(region.toString(), 0, 0L)));

        // yearStats의 content를 각 지역(row) 별로 생성, col에 들어갈 값들을 계산하여 저장
        returns.stream()
            .filter(c -> c.getCorrespondent().getCorrespondentCategory() == category)
            .forEach(c -> {
                YearStatsContent yearStatsContent = yearStatsContentMap.get(
                    c.getCorrespondent().getRegion().toString());
                yearStatsContent.updateCount();
                Long sumsPrice = c.getDetailContracts().stream()
                    .mapToLong(DetailContract::getTotalPrice)
                    .sum();
                yearStatsContent.updateSumsPrice(sumsPrice);
            });

        // list로 변환
        List<YearStatsContent> contents = yearStatsContentMap.values().stream().toList();

        // 이미 db에 해당 user_id, 거래처 카테고리, 연도로 계산한 데이터가 있으면 해당 엔티티를 갱신, 없다면 새로운 엔티티 생성 및 저장
        // 기존 데이터 업데이트 후 반환

        return statsRepository.findByUserId(userId)
            .stream()
            .filter(ys -> Objects.equals(ys.getYearNumber(), year) && ys.getCategory() == category)
            .findFirst()
            .map(ys -> {
                ys.updateContent(contents);
                return ys;  // 기존 데이터 업데이트 후 반환
            })
            .orElseGet(() -> statsRepository.save(
                new YearStats(null, year, category, userId, contents)
            ));
    }

}
