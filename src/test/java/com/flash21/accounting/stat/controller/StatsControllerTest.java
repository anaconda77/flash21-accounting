package com.flash21.accounting.stat.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.flash21.accounting.correspondent.domain.CorrespondentCategory;
import com.flash21.accounting.correspondent.domain.Region;
import com.flash21.accounting.stat.domain.YearStatsContent;
import com.flash21.accounting.stat.dto.response.YearStatsResponseDto;
import com.flash21.accounting.stat.service.YearStatsService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import static org.assertj.core.api.Assertions.assertThat;

class StatsControllerTest {

    private final YearStatsService yearStatsService = mock(YearStatsService.class);
    private final YearStatsController statsController = new YearStatsController(yearStatsService);

    @Test
    @DisplayName("통계 조회 테스트")
    void getYearStatistics() {
        Long userId = 1L;
        Integer year = 2025;
        CorrespondentCategory category = CorrespondentCategory.CAFE;

        YearStatsResponseDto responseDto = createMockResponseDto(userId, year, category);
        when(yearStatsService.getYearStatistics(any(), any(), any())).thenReturn(responseDto);

        ResponseEntity<YearStatsResponseDto> response = statsController.getYearStatistics(userId, category, year);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().userId()).isEqualTo(userId);
        assertThat(response.getBody().year()).isEqualTo(year);
        assertThat(response.getBody().category()).isEqualTo(category);
    }

    @Test
    @DisplayName("통계 강제 재계산 테스트")
    void createYearStatistics() {
        Long userId = 1L;
        Integer year = 2025;
        CorrespondentCategory category = CorrespondentCategory.CAFE;

        YearStatsResponseDto responseDto = createMockResponseDto(userId, year, category);
        when(yearStatsService.createYearStatistics(any(), any(), any())).thenReturn(responseDto);

        ResponseEntity<YearStatsResponseDto> response = statsController.createYearStatistics(userId, category, year);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().userId()).isEqualTo(userId);
        assertThat(response.getBody().year()).isEqualTo(year);
        assertThat(response.getBody().category()).isEqualTo(category);
    }

    private YearStatsResponseDto createMockResponseDto(Long userId, Integer year, CorrespondentCategory category) {
        List<YearStatsContent> contents = Arrays.stream(Region.values())
                .map(region -> new YearStatsContent(region.toString(), 1, 100000L))
                .toList();

        return YearStatsResponseDto.builder()
                .userId(userId)
                .year(year)
                .category(category)
                .content(contents)
                .build();
    }
}