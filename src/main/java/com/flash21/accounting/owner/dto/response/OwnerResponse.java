package com.flash21.accounting.owner.dto.response;

import com.flash21.accounting.owner.domain.Owner;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record OwnerResponse (
    Long ownerId,
    String name,
    String phoneNumber,
    String email,
    String faxNumber
){
    public static OwnerResponse fromEntity(Owner owner) {
        return OwnerResponse.builder()
                .ownerId(owner.getOwnerId())
                .name(owner.getName())
                .phoneNumber(owner.getPhoneNumber())
                .email(owner.getEmail())
                .faxNumber(owner.getFaxNumber())
                .build();
    }
}