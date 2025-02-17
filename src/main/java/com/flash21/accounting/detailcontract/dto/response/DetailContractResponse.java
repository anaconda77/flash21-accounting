package com.flash21.accounting.detailcontract.dto.response;

import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractCategory;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class DetailContractResponse {
    Long detailContractId;
    Long contractId;
    DetailContractCategory detailContractCategory;
    DetailContractStatus status;
    String content;
    Integer quantity;
    Integer unitPrice;
    Integer supplyPrice;
    Integer totalPrice;
    String paymentMethod;
    String paymentCondition;

    public static DetailContractResponse from(DetailContract detailContract) {
        return DetailContractResponse.builder()
                .detailContractId(detailContract.getDetailContractId())
                .contractId(detailContract.getContract().getContractId())
                .detailContractCategory(detailContract.getDetailContractCategory())
                .status(detailContract.getStatus())
                .content(detailContract.getContent())
                .quantity(detailContract.getQuantity())
                .unitPrice(detailContract.getUnitPrice())
                .supplyPrice(detailContract.getSupplyPrice())
                .totalPrice(detailContract.getTotalPrice())
                .paymentMethod(detailContract.getPayment().getMethod())
                .paymentCondition(detailContract.getPayment().getCondition())
                .build();
    }
}