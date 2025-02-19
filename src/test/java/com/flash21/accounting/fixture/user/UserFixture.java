package com.flash21.accounting.fixture.user;

import com.flash21.accounting.user.Role;
import com.flash21.accounting.user.User;

public class UserFixture {

    public static User createDefault() {
        return User.builder()
            .username("string")
            .password("string")
            .name("string")
            .phoneNumber("string")
            .email("string")
            .address("string")
            .addressDetail("string")
            .role(Role.ROLE_ADMIN)
            .grade("string")
            .companyPhoneNumber("string")
            .companyFaxNumber("string")
            .build();
    }
}
