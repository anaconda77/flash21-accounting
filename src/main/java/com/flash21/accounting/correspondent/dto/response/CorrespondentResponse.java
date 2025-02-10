package com.flash21.accounting.correspondent.dto.response;

import com.flash21.accounting.correspondent.model.Correspondent;

public record CorrespondentResponse(
    Long correspondentId,
    String correspondentName,
    String presidentName,
    String ownerName,
    String ownerPosition,
    String ownerEmail,
    String taxEmail,
    String businessRegNumber,
    String address,
    String detailedAddress,
    String memo
) {

}
