package com.flash21.accounting.stat.controller;

import com.flash21.accounting.correspondent.domain.CorrespondentCategory;
import com.flash21.accounting.stat.dto.response.YearStatsResponseDto;
import com.flash21.accounting.stat.service.YearStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stat")
@RequiredArgsConstructor
public class YearStatsController {

    private final YearStatsService yearStatsService;

    @GetMapping("/users/{userId}/categories/{category}/years/{year}")
    public ResponseEntity<YearStatsResponseDto> getYearStatistics(
            @PathVariable Long userId,
            @PathVariable CorrespondentCategory category,
            @PathVariable Integer year
    ) {
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
        return ResponseEntity.ok(
                yearStatsService.createYearStatistics(userId, category, year)
        );
    }
}