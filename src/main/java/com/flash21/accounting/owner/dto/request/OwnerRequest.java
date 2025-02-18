package com.flash21.accounting.owner.dto.request;

import com.flash21.accounting.owner.domain.Owner;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotNull;

public record OwnerRequest (
    @NotNull String name,
    @NotNull String phoneNumber,
    @NotNull String email,
    @NotNull String faxNumber
){
    public Owner toEntity(){
        return Owner.builder()
                .name(name)
                .phoneNumber(phoneNumber)
                .email(email)
                .faxNumber(faxNumber)
                .build();
    }
}