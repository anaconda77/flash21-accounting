package com.flash21.accounting.detailcontract.dto.request;

import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class StatusUpdateRequest {
    @NotNull(message = "상태는 필수입니다.")
    private DetailContractStatus status;
}
