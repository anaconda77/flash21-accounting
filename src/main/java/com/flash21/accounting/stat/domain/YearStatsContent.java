package com.flash21.accounting.stat.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class YearStatsContent {
    private String region;
    private Integer count;
    private Long sumsPrice;


    public void updateCount() {
        this.count++;
    }

    public void updateSumsPrice(Long sumsPrice) {
        this.sumsPrice += sumsPrice;
    }
}