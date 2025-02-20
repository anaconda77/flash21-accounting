package com.flash21.accounting.outsourcing.dto.response;

import com.flash21.accounting.outsourcing.domain.entity.Outsourcing;
import com.flash21.accounting.outsourcing.domain.entity.OutsourcingStatus;
import jakarta.persistence.GeneratedValue;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class OutsourcingResponse {
    Long outsourcingId;
    Long correspondentId;
    Long detailContractId;
    OutsourcingStatus status;
    String content;
    Integer quantity;
    Integer unitPrice;
    Integer supplyPrice;
    Integer totalPrice;

    public static OutsourcingResponse from(Outsourcing outsourcing) {
        return OutsourcingResponse.builder()
                .outsourcingId(outsourcing.getOutSourcingId())
                .correspondentId(outsourcing.getCorrespondent().getId())
                .detailContractId(outsourcing.getDetailContract().getDetailContractId())
                .status(outsourcing.getStatus())
                .content(outsourcing.getContent())
                .quantity(outsourcing.getQuantity())
                .unitPrice(outsourcing.getUnitPrice())
                .supplyPrice(outsourcing.getSupplyPrice())
                .totalPrice(outsourcing.getTotalPrice())
                .build();
    }
}
