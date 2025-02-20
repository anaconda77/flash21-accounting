package com.flash21.accounting.stat.dto.response;

import com.flash21.accounting.correspondent.domain.CorrespondentCategory;
import com.flash21.accounting.stat.domain.YearStatsContent;
import lombok.Builder;

import java.util.List;

@Builder
public record YearStatsResponseDto(
        Long id,
        Integer year,
        CorrespondentCategory category,
        Long userId,
        List<YearStatsContent> content
) {}
