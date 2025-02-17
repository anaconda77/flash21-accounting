package com.flash21.accounting.detailcontract.dto.request;

import com.flash21.accounting.detailcontract.domain.entity.DetailContractCategory;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DetailContractRequest {
    @NotNull(message = "계약서 ID는 필수입니다.")
    private Long contractId;

    @NotNull(message = "세부계약 상태는 필수입니다.")
    private DetailContractStatus status;

    // 한글명으로 받음
    @NotBlank(message = "세부계약서 카테고리는 필수입니다.")
    private String detailContractCategory;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    @NotNull(message = "수량은 필수입니다.")
    @Positive(message = "수량은 0보다 커야 합니다.")
    private Integer quantity;

    @NotNull(message = "단가는 필수입니다.")
    @Positive(message = "단가는 0보다 커야 합니다.")
    private Integer unitPrice;

    @NotNull(message = "공급가액은 필수입니다.")
    @Positive(message = "공급가액은 0보다 커야 합니다.")
    private Integer supplyPrice;

    @NotNull(message = "합계금액은 필수입니다.")
    @Positive(message = "합계금액은 0보다 커야 합니다.")
    private Integer totalPrice;

    // 지출 정보
    private String paymentMethod;
    private String paymentCondition;
}