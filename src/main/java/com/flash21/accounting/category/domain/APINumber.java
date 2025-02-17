package com.flash21.accounting.category.domain;

import java.util.Objects;
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

    public static boolean isNecessaryTypeId(APINumber apinumber) {
        if (apinumber.equals(CORRESPONDENT)) {
            return true;
        }
        return false;
    }

    public static APINumber getAPINumber(Integer apiNumber) {
        if (apiNumber == null) {
            return null;
        }

        for (APINumber apinumber : APINumber.values()) {
            if (Objects.equals(apinumber.apiNumber, apiNumber)) {
                return apinumber;
            }
        }
        return null;
    }
}
