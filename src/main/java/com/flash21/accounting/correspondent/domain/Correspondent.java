package com.flash21.accounting.correspondent.domain;

import com.flash21.accounting.correspondent.dto.request.CorrespondentRequest;
import com.flash21.accounting.owner.domain.Owner;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
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
    private String managerName;
    @Column(length = 20)
    private String managerPosition;
    @Column(length = 20)
    private String managerPhoneNumber;
    @Column(length = 50)
    private String managerEmail;
    @Column(length = 50)
    private String taxEmail;
    @Column(nullable = false, length = 50)
    private String businessRegNumber;
    private String address;
    private String detailedAddress;
    @Lob @Column(length = 65535, columnDefinition = "text")
    private String memo;
    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Owner owner;
    @Enumerated(EnumType.STRING)
    private CorrespondentCategory correspondentCategory;

    @Enumerated(EnumType.STRING)
    private CorrespondentType correspondentType;

    @Enumerated(EnumType.STRING)
    private Region region;

    public Correspondent(CorrespondentRequest request, Owner owner) {
        correspondentName = request.correspondentName();
        managerName = request.managerName();
        managerPosition = request.managerPosition();
        managerPhoneNumber = request.managerPhoneNumber();
        managerEmail = request.managerEmail();
        taxEmail = request.taxEmail();
        businessRegNumber = request.businessRegNumber();
        address = request.address();
        detailedAddress = request.detailedAddress();
        memo = request.memo();
        this.owner = owner;
        correspondentCategory = CorrespondentCategory.of(request.categoryName());
        correspondentType = CorrespondentType.of(request.type());
        region = Region.of(request.region());
    }

    public void updateCorrespondent(CorrespondentRequest request) {
        correspondentName = request.correspondentName();
        managerName = request.managerName();
        managerPosition = request.managerPosition();
        managerPhoneNumber = request.managerPhoneNumber();
        managerEmail = request.managerEmail();
        taxEmail = request.taxEmail();
        businessRegNumber = request.businessRegNumber();
        address = request.address();
        detailedAddress = request.detailedAddress();
        memo = request.memo();
        correspondentCategory = CorrespondentCategory.of(request.categoryName());
        correspondentType = CorrespondentType.of(request.type());
        region = Region.of(request.region());
    }
}

