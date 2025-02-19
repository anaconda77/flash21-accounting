package com.flash21.accounting.contract.entity;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ProcessStatus {
    PENDING_APPROVAL("결재진행"),
    CONTRACTED("계약진행"),
    WAITING("작업대기"),
    BILLING("청구(납품)"),
    AWAITING_PAYMENT("입금 후 진행"),
    DONE("최종완료");

    private final String name;

    public static ProcessStatus of(String name) {
        return Arrays.stream(ProcessStatus.values())
                .filter(p -> p.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
