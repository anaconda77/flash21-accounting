package com.flash21.accounting.contract.dto.response;

import com.flash21.accounting.contract.entity.ContractCategory;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
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
    private User lastModifyUser;
    private ContractCategory contractCategory;
    private ProcessStatus processStatus;
    private Method method;
    private String name;
    private LocalDate registerDate;
    private LocalDate contractStartDate;
    private LocalDate contractEndDate;
    private LocalDate workEndDate;
    private Correspondent correspondent;
    private String mainContractContent;

    public static ContractResponseDto of(Long contractId, User admin, User lastModifyUser, ContractCategory contractCategory,
                                         ProcessStatus processStatus, Method method, String name,
                                         LocalDate contractStartDate, LocalDate contractEndDate,
                                         LocalDate workEndDate, LocalDate registerDate, Correspondent correspondent, String mainContractContent) {
        return ContractResponseDto.builder()
                .contractId(contractId)
                .admin(admin)
                .lastModifyUser(lastModifyUser)
                .contractCategory(contractCategory)
                .processStatus(processStatus)
                .method(method)
                .name(name)
                .registerDate(registerDate)
                .contractStartDate(contractStartDate)
                .contractEndDate(contractEndDate)
                .workEndDate(workEndDate)
                .correspondent(correspondent)
                .mainContractContent(mainContractContent)
                .build();
    }
}
