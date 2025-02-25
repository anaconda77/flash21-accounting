package com.flash21.accounting.stat.dto.response;

import com.flash21.accounting.stat.domain.YearStatsContent;
import java.util.List;

public record AllYearStatsResponseDto(
    Long userId,
    List<YearStatsResponse> stats

) {

    public record YearStatsResponse(
        Integer year,
        List<CategoryStatsResponse> stats
    ) {

        public record CategoryStatsResponse(
            String category,
            List<YearStatsContent> content
        ) {

        }
    }
}
