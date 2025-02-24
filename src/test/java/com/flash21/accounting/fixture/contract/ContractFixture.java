package com.flash21.accounting.fixture.contract;

import com.flash21.accounting.contract.entity.Contract;
import com.flash21.accounting.contract.entity.ContractCategory;
import com.flash21.accounting.contract.entity.Method;
import com.flash21.accounting.contract.entity.ProcessStatus;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.user.User;
import java.time.LocalDate;
import java.time.Month;

public class ContractFixture {

    public static Contract createDefault(User user, Correspondent correspondent) {
        return Contract.builder()
            .admin(user)
            .method(Method.GENERAL)
            .processStatus(ProcessStatus.WAITING)
            .name("계약서 1")
            .contractStartDate(LocalDate.of(2025, Month.JANUARY,1))
            .contractEndDate(LocalDate.now())
            .workEndDate(LocalDate.now())
            .correspondent(correspondent)
            .contractCategory(ContractCategory.DEVELOP)
            .registerDate(LocalDate.now())
            .lastModifyUser(user)
            .build();
    }
}
