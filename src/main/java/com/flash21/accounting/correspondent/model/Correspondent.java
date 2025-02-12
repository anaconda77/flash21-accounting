package com.flash21.accounting.correspondent.model;

import com.flash21.accounting.correspondent.dto.request.CorrespondentRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "UK_CORRESPONDENT_NAME", columnNames = "correspondentName")
})
@Getter
@Builder(access = AccessLevel.PUBLIC)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Correspondent {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 50)
    private String correspondentName;
    @Column(length = 20)
    private String presidentName;
    @Column(length = 20)
    private String ownerName;
    @Column(length = 20)
    private String ownerPosition;
    @Column(length = 20)
    private String ownerPhoneNumber;
    @Column(length = 50)
    private String ownerEmail;
    @Column(length = 50)
    private String taxEmail;
    @Column(nullable = false, length = 50)
    private String businessRegNumber;
    private String address;
    private String detailedAddress;
    @Lob @Column(length = 65535)
    private String memo;


    public void updateCorrespondent(CorrespondentRequest request) {
        correspondentName = request.correspondentName();
        presidentName = request.presidentName();
        ownerName = request.ownerName();
        ownerPosition = request.ownerPosition();
        ownerPhoneNumber = request.ownerPhoneNumber();
        ownerEmail = request.ownerEmail();
        taxEmail = request.taxEmail();
        businessRegNumber = request.businessRegNumber();
        address = request.address();
        detailedAddress = request.detailedAddress();
        memo = request.memo();
    }
}

