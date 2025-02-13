package com.flash21.accounting.contract.dto;

import com.flash21.accounting.category.domain.Category;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.contract.entity.Status;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContractResponseDto {
    private Long contractId;
    private User admin;
    private Category category;
    private Status status;
    private ProcessStatus processStatus;
    private Method method;
    private String name;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private LocalDate workEndDate;
    private Correspondent correspondent;

    public static ContractResponseDto of(Long contractId, User admin, Category category, Status status,
                                         ProcessStatus processStatus, Method method, String name,
                                         LocalDate contractStartDate, LocalDate contractEndDate,
                                         LocalDate workEndDate, Correspondent correspondent) {
        return ContractResponseDto.builder()
                .contractId(contractId)
                .admin(admin)
                .category(category)
                .status(status)
                .processStatus(processStatus)
                .method(method)
                .name(name)
                .contractStartDate(contractStartDate)
                .contractEndDate(contractEndDate)
                .workEndDate(workEndDate)
                .correspondent(correspondent)
                .build();
    }
}
