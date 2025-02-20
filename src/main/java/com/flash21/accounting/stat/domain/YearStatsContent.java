package com.flash21.accounting.stat.domain;

import lombok.Data;

@Data
public class YearStatsContent {
    private String region;
    private Integer count;
    private Long sumsPrice;
}