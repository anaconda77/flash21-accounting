package com.flash21.accounting.correspondent.dto.request;

public record CorrespondentRequest(
    String correspondentName,
    String presidentName,
    String ownerName,
    String ownerPosition,
    String ownerPhoneNumber,
    String ownerEmail,
    String taxEmail,
    String businessRegNumber,
    String address,
    String detailedAddress,
    String memo
) {

}
