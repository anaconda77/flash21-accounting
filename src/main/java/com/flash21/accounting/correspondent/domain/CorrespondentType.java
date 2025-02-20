package com.flash21.accounting.correspondent.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum CorrespondentType {
    CONTRACTING("수주"), OUTSOURCING("외주");

    private final String name;

    public static CorrespondentType of(String name) {
        return Arrays.stream(CorrespondentType.values())
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
}
