package com.flash21.accounting.contract.dto;

import com.flash21.accounting.contract.entity.Status;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractRequestDto {
    private Long adminId;  // Integer → Long 변경 (User ID와 일치)
    private Integer headSignId;
    private Integer directorSignId;
    private String category;
    private Status status;
    private String name;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private LocalDate workEndDate;
    private Integer categoryId;
    private Integer correspondentId;
}
