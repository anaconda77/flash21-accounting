package com.flash21.accounting.stat.controller;

import com.flash21.accounting.correspondent.domain.CorrespondentCategory;
import com.flash21.accounting.stat.dto.response.YearStatsResponseDto;
import com.flash21.accounting.stat.service.YearStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stat")
@RequiredArgsConstructor
public class YearStatsController {

    private final YearStatsService yearStatsService;

    @GetMapping("/users/{userId}/categories/{category}/years/{year}")
    public ResponseEntity<YearStatsResponseDto> getYearStatistics(
            @PathVariable Long userId,
            @PathVariable CorrespondentCategory category,
            @PathVariable(required = false) Integer year
    ) {
        if (!YearStatsService.allYears.contains(year)) {
            throw new IllegalArgumentException("일치하지 않는 연도입니다. 연도의 값은 다음과 같아야 합니다 : " + YearStatsService.allYears);
        }

        return ResponseEntity.ok(
                yearStatsService.getYearStatistics(userId, category, year)
        );
    }

    @PostMapping("/users/{userId}/categories/{category}/years/{year}")
    public ResponseEntity<YearStatsResponseDto> createYearStatistics(
            @PathVariable Long userId,
            @PathVariable CorrespondentCategory category,
            @PathVariable Integer year
    ) {
        if (!YearStatsService.allYears.contains(year)) {
            throw new IllegalArgumentException("일치하지 않는 연도입니다. 연도의 값은 다음과 같아야 합니다 : " + YearStatsService.allYears);
        }

        return ResponseEntity.ok(
                yearStatsService.createYearStatistics(userId, category, year)
        );
    }
}