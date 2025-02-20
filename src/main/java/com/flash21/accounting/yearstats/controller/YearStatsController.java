package com.flash21.accounting.yearstats.controller;

import com.flash21.accounting.yearstats.dto.response.YearStatsResponseDto;
import com.flash21.accounting.yearstats.service.YearStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/year-stats")
@RequiredArgsConstructor
public class YearStatsController {

    private final YearStatsService yearStatsService;

    /**
     * 특정 연도와 카테고리의 계약 통계를 조회
     * - year, category가 없으면 전체 데이터 반환
     */
    @GetMapping
    public ResponseEntity<List<YearStatsResponseDto>> getYearStats(
            @RequestParam(required = false) Optional<Integer> year,
            @RequestParam(required = false) Optional<String> category) {

        List<YearStatsResponseDto> stats = yearStatsService.getYearStats(year, category);
        return ResponseEntity.ok(stats);
    }
}
