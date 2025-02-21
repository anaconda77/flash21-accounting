package com.flash21.accounting.outsourcing.domain.entity;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.OutsourcingErrorCode;
import com.flash21.accounting.common.status.StatusType;
import com.flash21.accounting.common.status.StatusValidator;

import java.io.Serializable;
import java.util.Arrays;

public enum OutsourcingStatus implements StatusType{
    TEMPORARY("임시"),
    ONGOING("진행"),
    DONE("완료"),
    CANCELED("취소");

    private final String displayName;

    OutsourcingStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public static OutsourcingStatus fromDisplayName(String displayName) {
        return StatusValidator.fromDisplayName(
                OutsourcingStatus.class,
                displayName,
                () -> new AccountingException(OutsourcingErrorCode.INVALID_STATUS)
        );
    }

    public static void validateStatusTransition(OutsourcingStatus currentStatus, OutsourcingStatus newStatus) {
        StatusValidator.validateStatusTransition(
                currentStatus,
                newStatus,
                new AccountingException(OutsourcingErrorCode.CANNOT_UPDATE_CANCELED_CONTRACT),
                new AccountingException(OutsourcingErrorCode.INVALID_STATUS_TRANSITION),
                new AccountingException(OutsourcingErrorCode.CANNOT_UPDATE_DONE_CONTRACT)
        );
    }

}
