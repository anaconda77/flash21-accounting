package com.flash21.accounting.detailcontract.domain.entity;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.DetailContractErrorCode;

import java.util.Arrays;

public enum DetailContractStatus {
    TEMPORARY("임시"),
    ONGOING("진행"),
    DONE("완료"),
    CANCELED("취소");

    private String displayName;

    DetailContractStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static DetailContractStatus fromDisplayName(String displayName) {
        return Arrays.stream(DetailContractStatus.values())
                .filter(status -> status.getDisplayName().equals(displayName))
                .findFirst()
                .orElseThrow(() -> new AccountingException(DetailContractErrorCode.INVALID_STATUS));
    }
}
