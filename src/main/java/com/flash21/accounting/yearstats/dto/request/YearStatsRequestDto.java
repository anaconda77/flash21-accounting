package com.flash21.accounting.yearstats.dto.request;

import lombok.Builder;

@Builder
public record YearStatsRequestDto(
        Integer year,
        String category,
        Integer userId
) {}
