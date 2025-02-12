package com.flash21.accounting.detailcontract.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateDetailContractResponse {
    private String message;

    public static UpdateDetailContractResponse success() {
        return UpdateDetailContractResponse.builder()
                .message("수정이 완료되었습니다")
                .build();
    }
}