package com.flash21.accounting.owner.domain;

import com.flash21.accounting.owner.dto.request.OwnerRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "owner")
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "owner_id", nullable = false, updatable = false)
    private Long ownerId;

    @Column(name = "owner_name", nullable = false)
    private String name;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "fax_number")
    private String faxNumber;

    @Builder
    public Owner(Long ownerId, String name, String phoneNumber, String email, String faxNumber) {
        this.ownerId = ownerId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.faxNumber = faxNumber;
    }

    public void update(OwnerRequest ownerRequest) {
        this.name = ownerRequest.name();
        this.phoneNumber = ownerRequest.phoneNumber();
        this.email = ownerRequest.email();
        this.faxNumber = ownerRequest.faxNumber();
    }
}
