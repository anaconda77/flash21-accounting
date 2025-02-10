package com.flash21.accounting.contract.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor  // ✅ 기본 생성자 추가
@AllArgsConstructor // ✅ 모든 필드 포함 생성자 자동 생성
public class ContractResponseDto {
    private Integer contractId;
    private String category;
    private String status;
    private String name;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private LocalDate workEndDate;
}
