package com.flash21.accounting.correspondent.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CorrespondentRequest(
    @NotBlank(message = "거래처명을 입력해주세요.")
    @Size(max = 50, message = "거래처명은 최대 50자까지 입력가능합니다.")
    String correspondentName,
    @Size(max = 20, message = "대표자명은 최대 20자까지 입력가능합니다.")
    String presidentName,
    @Size(max = 20, message = "담당자명은 최대 20자까지 입력가능합니다.")
    String ownerName,
    @Size(max = 20, message = "담당자 직책은 최대 20자까지 입력가능합니다.")
    String ownerPosition,
    @Size(max = 20, message = "담당자 전화번호는 최대 20자까지 입력가능합니다.")
    String ownerPhoneNumber,
    @Size(max = 50, message = "담당자 이메일은 최대 50자까지 입력가능합니다.")
    String ownerEmail,
    @Size(max = 50, message = "세금 계산서 이메일은 최대 50자까지 입력가능합니다.")
    String taxEmail,
    @NotBlank(message = "사업자등록번호를 입력해주세요.")
    @Size(max = 50, message = "사업자등록번호는 최대 50자까지 입력가능합니다.")
    String businessRegNumber,
    @Size(max = 255, message = "주소, 입력가능한 길이를 초과하였습니다.")
    String address,
    @Size(max = 255, message = "상세주소, 입력가능한 길이를 초과하였습니다.")
    String detailedAddress,
    @Size(max = 65535, message = "입력가능한 길이를 초과하였습니다.")
    String memo
) {

}
