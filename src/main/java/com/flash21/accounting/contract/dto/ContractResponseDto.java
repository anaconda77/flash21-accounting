package com.flash21.accounting.contract.dto;

import com.flash21.accounting.category.domain.Category;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.contract.entity.Status;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponseDto {
    private User adminId;
    private Long contractId;
    private Category category;
    private Status status;
    private ProcessStatus processStatus;
    private String name;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private LocalDate workEndDate;
    private Correspondent correspondentId;
}
