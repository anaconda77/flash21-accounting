package com.flash21.accounting.contract.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

public enum ProcessStatus {
    PENDING_APPROVAL("결재진행"),
    CONTRACTED("계약진행"),
    WAITING("작업대기"),
    BILLING("청구(납품)"),
    AWAITING_PAYMENT("입금 후 진행"),
    DONE("최종완료");

    private final String name;

    ProcessStatus(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static ProcessStatus fromString(String value) {
        return Arrays.stream(ProcessStatus.values())
                .filter(p -> p.getName().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원되지 않는 진행 상태: " + value));
    }
}
