package com.flash21.accounting.fixture;

import com.flash21.accounting.owner.domain.Owner;

public class OwnerFixture {

    public static Owner createDefault() {
        return Owner.builder()
                .ownerId(1L)
                .name("name")
                .build();
//                .name("테스트 소유자_" + System.currentTimeMillis())  // 유니크한 이름 보장
//                .phoneNumber("010-1234-5678")
//                .email("owner@test.com")
//                .faxNumber("02-1234-5678")
//                .build();
    }
}
