package com.flash21.accounting.contract.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ContractDto {
    private Long contractId;
    private String category;
    private String status;
    private String name;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private LocalDate workEndDate;
}
