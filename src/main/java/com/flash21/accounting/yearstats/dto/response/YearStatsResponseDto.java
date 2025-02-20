package com.flash21.accounting.yearstats.dto.response;

import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
public record YearStatsResponseDto(
        Integer id,
        Integer year,
        String category,
        Integer userId,
        List<Map<String, Object>> content // JSON 데이터를 List<Map>으로 처리
) {}
