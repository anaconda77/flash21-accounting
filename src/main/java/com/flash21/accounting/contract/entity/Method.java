package com.flash21.accounting.contract.entity;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Method {
    GENERAL("일반계약"),
    BID("입찰계약"),
    OUTSOURCING("하도급계약");

    private final String name;

    public static Method of(String name) {
        return Arrays.stream(Method.values())
                .filter(m -> m.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
