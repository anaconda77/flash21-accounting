package com.flash21.accounting.outsourcing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OutsourcingRequest {
    @NotNull(message = "거래처 ID는 필수입니다.")
    private Long correspondentId;
    @NotBlank(message = "외주계약 상태는 필수입니다.")
    String status;
    @NotBlank(message = "내용은 필수입니다.")
    String content;
    @NotNull(message = "수량은 필수입니다.")
    @Positive(message = "수량은 0보다 커야합니다.")
    Integer quantity;
    @NotNull(message = "단가는 필수입니다.")
    @Positive(message = "단가은 0보다 커야합니다.")
    Integer unitPrice;
    @NotNull(message = "공급가액은 필수입니다")
    @Positive(message = "공급가액은 0보다 커야합니다.")
    Integer supplyPrice;
    @NotNull(message = "합계금액은 필수입니다")
    @Positive(message = "합계금액은 0보다 커야합니다.")
    Integer totalPrice;
}
