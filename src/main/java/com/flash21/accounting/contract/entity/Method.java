package com.flash21.accounting.contract.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

public enum Method {
    GENERAL("일반계약"),
    BID("입찰계약"),
    OUTSOURCING("하도급계약");

    private final String name;

    Method(String name) {
        this.name = name;
    }

    @JsonValue
    public String getName() {
        return name;
    }

    @JsonCreator
    public static Method fromString(String value) {
        return Arrays.stream(Method.values())
                .filter(m -> m.getName().equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원되지 않는 계약 방식: " + value));
    }
}
