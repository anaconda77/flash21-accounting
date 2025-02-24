package com.flash21.accounting.stat.controller;

import com.flash21.accounting.correspondent.domain.CorrespondentCategory;
import com.flash21.accounting.stat.dto.request.YearStatsRequestDto;
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

    @GetMapping
    public ResponseEntity<YearStatsResponseDto> getYearStatistics(
            @RequestParam Long userId,
            @RequestParam CorrespondentCategory category,
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(
                yearStatsService.getYearStatistics(userId, category, year)
        );
    }

    @PostMapping
    public ResponseEntity<YearStatsResponseDto> createYearStatistics(
            @RequestParam Long userId,
            @RequestParam CorrespondentCategory category,
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(
                yearStatsService.createYearStatistics(userId, category, year)
        );
    }
}