package com.flash21.accounting.correspondent.dto.response;

public record CorrespondentResponse(
    Long correspondentId,
    String correspondentName,
    owner owner,
    String managerName,
    String managerPosition,
    String managerEmail,
    String taxEmail,
    String businessRegNumber,
    String address,
    String detailedAddress,
    String memo,
    String categoryName,
    String type,
    String region
) {
    public record owner (
        Long ownerId,
        String ownerName
    ) { }
}
