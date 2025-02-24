package com.flash21.accounting.stat.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.flash21.accounting.common.ErrorCodeAssertions;
import com.flash21.accounting.common.exception.errorcode.StatsErrorCode;
import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.domain.CorrespondentCategory;
import com.flash21.accounting.correspondent.domain.Region;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.stat.domain.YearStats;
import com.flash21.accounting.stat.domain.YearStatsContent;
import com.flash21.accounting.stat.dto.response.YearStatsResponseDto;
import com.flash21.accounting.stat.repository.StatsRepository;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @InjectMocks
    private YearStatsService yearStatsService;

    @Mock
    private StatsRepository statsRepository;

    private Long userId;
    private Integer year;
    private CorrespondentCategory category;
    private List<YearStatsContent> contents;
    private YearStats yearStats;

    @BeforeEach
    void setUp() {
        userId = 1L;
        year = 2025;
        category = CorrespondentCategory.CAFE;

        contents = Arrays.stream(Region.values())
                .map(region -> new YearStatsContent(region.toString(), 1, 100000L))
                .toList();

        yearStats = new YearStats(1L, year, category, userId, contents);
    }

    @DisplayName("통계 조회 성공 테스트 - 기존 데이터 존재")
    @Test
    void getYearStatistics_ExistingData() {
        when(statsRepository.findByUserId(userId))
                .thenReturn(List.of(yearStats));

        YearStatsResponseDto result = yearStatsService.getYearStatistics(userId, category, year);

        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(userId);
        assertThat(result.year()).isEqualTo(year);
        assertThat(result.category()).isEqualTo(category);
        assertThat(result.content()).isEqualTo(contents);
    }

    @DisplayName("통계 조회 성공 테스트 - 새로운 데이터 계산")
    @Test
    void getYearStatistics_CalculateNewData() {
        when(statsRepository.findByUserId(userId))
                .thenReturn(new ArrayList<>());

        // Correspondent
        Correspondent mockCorrespondent = mock(Correspondent.class);
        when(mockCorrespondent.getCorrespondentCategory()).thenReturn(category);
        when(mockCorrespondent.getRegion()).thenReturn(Region.SEOUL);

        // Contract
        Contract mockContract = mock(Contract.class);
        when(mockContract.getCorrespondent()).thenReturn(mockCorrespondent);

        // DetailContracts
        DetailContract mockDetail = mock(DetailContract.class);
        when(mockDetail.getTotalPrice()).thenReturn(100000);
        when(mockContract.getDetailContracts()).thenReturn(List.of(mockDetail));

        when(statsRepository.getContracts(userId, year))
                .thenReturn(List.of(mockContract));

        when(statsRepository.save(any(YearStats.class))).thenReturn(yearStats);

        YearStatsResponseDto result = yearStatsService.getYearStatistics(userId, category, year);

        assertThat(result).isNotNull();
        verify(statsRepository).save(any(YearStats.class));
    }

    @DisplayName("통계 강제 재계산 성공 테스트")
    @Test
    void createYearStatistics_Success() {
        // Correspondent
        Correspondent mockCorrespondent = mock(Correspondent.class);
        when(mockCorrespondent.getCorrespondentCategory()).thenReturn(category);
        when(mockCorrespondent.getRegion()).thenReturn(Region.SEOUL);

        // Contract
        Contract mockContract = mock(Contract.class);
        when(mockContract.getCorrespondent()).thenReturn(mockCorrespondent);

        // DetailContracts
        DetailContract mockDetail = mock(DetailContract.class);
        when(mockDetail.getTotalPrice()).thenReturn(100000);
        when(mockContract.getDetailContracts()).thenReturn(List.of(mockDetail));

        when(statsRepository.getContracts(userId, year))
                .thenReturn(List.of(mockContract));

        when(statsRepository.save(any(YearStats.class))).thenReturn(yearStats);

        YearStatsResponseDto result = yearStatsService.createYearStatistics(userId, category, year);

        assertThat(result).isNotNull();
        verify(statsRepository).save(any(YearStats.class));
    }

    @DisplayName("통계 계산 실패 테스트 - 데이터 없음")
    @Test
    void calculateStatistics_NoData() {
        when(statsRepository.getContracts(userId, year))
                .thenReturn(new ArrayList<>());

        ErrorCodeAssertions.assertErrorCode(
                StatsErrorCode.CANNOT_CALCULATE_STATS,
                () -> yearStatsService.createYearStatistics(userId, category, year)
        );
    }
}