package com.flash21.accounting.fixture.contract;

import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.detailcontract.domain.entity.DetailContract;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractCategory;
import com.flash21.accounting.detailcontract.domain.entity.DetailContractStatus;
import com.flash21.accounting.detailcontract.domain.entity.Payment;

public class DetailContractFixture {

    public static DetailContract createWithPriceAndQuantity(Contract contract, Integer price, Integer quantity) {
        return DetailContract.builder()
            .detailContractCategory(DetailContractCategory.HOSTING_SERVICE)
            .contract(contract)
            .status(DetailContractStatus.ONGOING)
            .content("content...")
            .quantity(quantity)
            .unitPrice(price)
            .supplyPrice(price)
            .totalPrice(price*quantity)
            .build();
    }
}
