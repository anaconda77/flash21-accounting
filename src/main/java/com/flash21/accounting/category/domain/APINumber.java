package com.flash21.accounting.category.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum APINumber {

    USER(1),
    CONTRACT(2),
    CORRESPONDENT(3),
    OUTSOURCING(4), ;

    private final Integer apiNumber;

}
