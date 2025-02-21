package com.flash21.accounting.detailcontract.domain.entity;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.DetailContractErrorCode;
import com.flash21.accounting.common.status.StatusType;
import com.flash21.accounting.common.status.StatusValidator;

public enum DetailContractStatus implements StatusType {
    TEMPORARY("임시"),
    ONGOING("진행"),
    DONE("완료"),
    CANCELED("취소");

    private final String displayName;

    DetailContractStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String getDisplayName() {
        return displayName;
    }

    public static DetailContractStatus fromDisplayName(String displayName) {
        return StatusValidator.fromDisplayName(
                DetailContractStatus.class,
                displayName,
                () -> new AccountingException(DetailContractErrorCode.INVALID_STATUS)
        );
    }

    public static void validateStatusTransition(DetailContractStatus currentStatus, DetailContractStatus newStatus) {
        StatusValidator.validateStatusTransition(
                currentStatus,
                newStatus,
                new AccountingException(DetailContractErrorCode.CANNOT_UPDATE_CANCELED_CONTRACT),
                new AccountingException(DetailContractErrorCode.INVALID_STATUS_TRANSITION),
                new AccountingException(DetailContractErrorCode.CANNOT_UPDATE_DONE_CONTRACT)
        );
    }

}
