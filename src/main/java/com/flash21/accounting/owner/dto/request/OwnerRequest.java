package com.flash21.accounting.owner.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OwnerRequest {
    private String name;
    private String phoneNumber;
    private String email;
    private String faxNumber;
}
