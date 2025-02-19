package com.flash21.accounting.fixture;

import com.flash21.accounting.owner.domain.Owner;

public class OwnerFixture {

    public static Owner createDefault() {
        return Owner.builder()
                .ownerId(1L)
                .name("name")
                .build();
    }
}
