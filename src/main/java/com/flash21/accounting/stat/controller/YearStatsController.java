package com.flash21.accounting.stat.controller;

import com.flash21.accounting.correspondent.domain.CorrespondentCategory;
import com.flash21.accounting.stat.dto.response.AllYearStatsResponseDto;
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

    @GetMapping
    public ResponseEntity<AllYearStatsResponseDto> getAllStatistics(
        @RequestParam("userId") Long userId) {
        return ResponseEntity.ok(
            yearStatsService.getAllUserStats(userId)
        );
    }

    @GetMapping
    public ResponseEntity<YearStatsResponseDto> getYearStatistics(
        @RequestParam("userId") Long userId,
        @RequestParam("category") CorrespondentCategory category,
        @RequestParam("year") Integer year
    ) {
        return ResponseEntity.ok(
            yearStatsService.getYearStatistics(userId, category, year)
        );
    }

    @PostMapping
    public ResponseEntity<YearStatsResponseDto> createYearStatistics(
        @RequestParam("userId") Long userId,
        @RequestParam("category") CorrespondentCategory category,
        @RequestParam("year") Integer year
    ) {
        return ResponseEntity.ok(
            yearStatsService.createYearStatistics(userId, category, year)
        );
    }
}