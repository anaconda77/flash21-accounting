package com.flash21.accounting.contract.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

public enum ContractCategory {
    DEVELOP("개발"),
    MAINTENANCE("유지관리"),
    VIDEO("영상 제작"),
    PRINTS("인쇄물"),
    HOSTING("호스팅"),
    ETC("기타");

    private final String name;

    ContractCategory(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static ContractCategory fromString(String value) {
        return Arrays.stream(ContractCategory.values())
                .filter(c -> c.getName().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원되지 않는 계약 카테고리: " + value));
    }
}
