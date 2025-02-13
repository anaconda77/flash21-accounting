package com.flash21.accounting.contract.dto;

import com.flash21.accounting.category.domain.Category;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.contract.entity.Status;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ContractRequestDto {
    private Long adminId;  // Integer → Long 변경 (User ID와 일치)
    private Integer writerSignId;
    private Integer headSignId;
    private Integer directorSignId;
    private Long categoryId;
    private Status status;
    private ProcessStatus processStatus;
    private String method;
    private String name;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private LocalDate workEndDate;
    private Integer correspondentId;
}
