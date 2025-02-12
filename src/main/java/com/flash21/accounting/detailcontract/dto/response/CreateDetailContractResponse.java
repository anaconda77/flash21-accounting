package com.flash21.accounting.detailcontract.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateDetailContractResponse {
    private String message;
    private Long contractId;

    public static CreateDetailContractResponse of(Long contractId) {
        return CreateDetailContractResponse.builder()
                .message("세부계약서가 성공적으로 등록되었습니다.")
                .contractId(contractId)
                .build();
    }
}