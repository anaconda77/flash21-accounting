package com.flash21.accounting.contract.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.contract.entity.Status;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@Builder
public class ContractRequestDto {
    private Long adminId;
    private Integer writerSignId;
    private Integer headSignId;
    private Integer directorSignId;
    private Long categoryId;
    private Status status;
    private ProcessStatus processStatus;
    private Method method;
    private String name;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate contractStartDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate contractEndDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate workEndDate;
    private Integer correspondentId;

    public ContractRequestDto(Long adminId, Integer writerSignId, Integer headSignId, Integer directorSignId,
                              Long categoryId, Status status, ProcessStatus processStatus, Method method,
                              String name, LocalDate contractStartDate, LocalDate contractEndDate,
                              LocalDate workEndDate, Integer correspondentId) {
        this.adminId = adminId;
        this.writerSignId = writerSignId;
        this.headSignId = headSignId;
        this.directorSignId = directorSignId;
        this.categoryId = categoryId;
        this.status = status;
        this.processStatus = processStatus;
        this.method = method;
        this.name = name;
        this.contractStartDate = contractStartDate;
        this.contractEndDate = contractEndDate;
        this.workEndDate = workEndDate;
        this.correspondentId = correspondentId;
    }
}
