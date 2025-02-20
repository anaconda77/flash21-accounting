package com.flash21.accounting.outsourcing.dto.request;

import lombok.*;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class OutsourcingUpdateRequest {
    String status;
    String content;
    Integer quantity;
    Integer unitPrice;
    Integer supplyPrice;
    Integer totalPrice;
}
