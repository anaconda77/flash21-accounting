package com.flash21.accounting.user;

import jakarta.validation.constraints.NotNull;

public record UserUpdateDto(
        @NotNull Long id, @NotNull String password,
        @NotNull String name, @NotNull String phoneNumber,
        @NotNull String email, @NotNull String address,
        @NotNull String addressDetail, @NotNull Role role,
        @NotNull String grade, @NotNull String companyPhoneNumber,
        @NotNull String companyFaxNumber
) {
}
