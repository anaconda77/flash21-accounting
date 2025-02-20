package com.flash21.accounting.contract.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum Region {
    SEOUL("서울"),
    INCHEON("인천"),
    DAEJEON("대전"),
    DAEGU("대구"),
    BUSAN("부산"),
    ULSAN("울산"),
    GWANGJU("광주"),
    SEJONG("세종");

    private final String name;

    Region(String name) {
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
                .orElseThrow(() -> new IllegalArgumentException("지원되지 않는 지역: " + value));
    }
}
