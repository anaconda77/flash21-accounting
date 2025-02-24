package com.flash21.accounting.stat.dto.response;

import com.flash21.accounting.correspondent.domain.CorrespondentCategory;
import com.flash21.accounting.stat.domain.YearStats;
import com.flash21.accounting.stat.domain.YearStatsContent;
import lombok.Builder;

import java.util.List;

@Builder
public record YearStatsResponseDto(
        Long userId,
        Integer year,
        CorrespondentCategory category,
        List<YearStatsContent> content
) {

    public static YearStatsResponseDto of(YearStats yearStats) {
        return YearStatsResponseDto.builder()
            .userId(yearStats.getUserId())
            .year(yearStats.getYearNumber())
            .category(yearStats.getCategory())
            .content(yearStats.getContent())
            .build();
    }
}
