package com.flash21.accounting.user;

import jakarta.validation.constraints.NotNull;

public record UserRegisterDto(
        @NotNull String username, @NotNull String password,
        @NotNull String name, @NotNull String phoneNumber,
        @NotNull String email, @NotNull String address,
        @NotNull String addressDetail, @NotNull Role role,
        @NotNull String grade, @NotNull String companyPhoneNumber,
        @NotNull String companyFaxNumber
) {
    public User toEntity(String hashedPassword) {
        return User.builder()
                .username(this.username)
                .password(hashedPassword)
                .name(this.name)
                .phoneNumber(this.phoneNumber)
                .email(this.email)
                .address(this.address)
                .addressDetail(this.addressDetail)
                .role(this.role)
                .grade(this.grade)
                .companyPhoneNumber(this.companyPhoneNumber)
                .companyFaxNumber(this.companyFaxNumber)
                .build();
    }
}
