package com.flash21.accounting.user;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UserReadDto(
        Long id,
        String name,
        String phoneNumber,
        String email,
        String address,
        String addressDetail,
        Role role,
        String grade,
        String companyPhoneNumber,
        String companyFaxNumber
) {
    public static UserReadDto fromEntity(User user) {
        return UserReadDto.builder()
                .id(user.getId())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .email(user.getEmail())
                .address(user.getAddress())
                .addressDetail(user.getAddressDetail())
                .role(user.getRole())
                .grade(user.getGrade())
                .companyPhoneNumber(user.getCompanyPhoneNumber())
                .companyFaxNumber(user.getCompanyFaxNumber())
                .build();
    }
}
