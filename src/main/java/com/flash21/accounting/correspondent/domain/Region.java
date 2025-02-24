package com.flash21.accounting.correspondent.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.flash21.accounting.contract.entity.Method;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
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

    public static Region of(String name) {
        return Arrays.stream(Region.values())
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
