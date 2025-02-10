package com.flash21.accounting.contract.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractRequestDto {
    private Integer adminId;
    private Integer headSignId;
    private Integer directorSignId;
    private String category;
    private String status;
    private String name;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private LocalDate workEndDate;
    private Integer categoryId;
    private Integer correspondentId;
}
