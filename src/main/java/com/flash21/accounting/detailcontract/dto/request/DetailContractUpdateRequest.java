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
public class DetailContractUpdateRequest {
    private DetailContractStatus status;
    private DetailContractCategory detailContractCategory;
    private String content;
    private Integer quantity;
    private Integer unitPrice;
    private Integer supplyPrice;
    private Integer totalPrice;
    private String paymentMethod;
    private String paymentCondition;
}
