package com.flash21.accounting.outsourcing.domain.entity;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.OutsourcingErrorCode;

import java.util.Arrays;

public enum OutsourcingStatus {
    TEMPORARY("임시"),
    ONGOING("진행"),
    DONE("완료"),
    CANCELED("취소");

    private String displayName;

    OutsourcingStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static OutsourcingStatus fromDisplayName(String displayName) {
        return Arrays.stream(OutsourcingStatus.values())
                .filter(status -> status.getDisplayName().equals(displayName))
                .findFirst()
                .orElseThrow(() -> new AccountingException(OutsourcingErrorCode.INVALID_STATUS));
    }
}
