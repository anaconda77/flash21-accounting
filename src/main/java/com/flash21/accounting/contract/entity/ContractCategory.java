package com.flash21.accounting.contract.entity;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ContractCategory {
    DEVELOP("개발"),
    MAINTENANCE("유지관리"),
    VIDEO("영상 제작"),
    PRINTS("인쇄물"),
    HOSTING("호스팅"),
    ETC("기타");

    private final String name;

    public static ContractCategory of(String name) {
        return Arrays.stream(ContractCategory.values())
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
