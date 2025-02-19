package com.flash21.accounting.correspondent.domain;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum CorrespondentCategory {
    HEALTH("헬스장"), SWIM("수영장"), RESTAURANT("식당"), CAFE("카페");

    private final String name;

    public static CorrespondentCategory of(String name) {
        return Arrays.stream(CorrespondentCategory.values())
            .filter(c -> c.getName().equals(name))
            .findFirst()
            .orElse(null);
    }
}
