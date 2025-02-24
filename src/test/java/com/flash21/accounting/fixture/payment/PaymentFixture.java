package com.flash21.accounting.fixture.payment;

import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.Payment;

public class PaymentFixture {
    public static Payment createDefault(DetailContract detailContract) {
        return Payment.builder()
            .method("카드")
            .condition("100%")
            .detailContract(detailContract)
            .build();
    }

}
