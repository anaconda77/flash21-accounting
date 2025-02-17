package com.flash21.accounting.owner.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerResponse {
    private Integer ownerId;
    private String name;
    private String phoneNumber;
    private String email;
    private String faxNumber;
}
