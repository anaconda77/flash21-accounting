package com.flash21.accounting.fixture.correspondent;

import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.domain.CorrespondentCategory;
import com.flash21.accounting.fixture.OwnerFixture;
import com.flash21.accounting.owner.domain.Owner;

public class CorrespondentFixture {
    public static Correspondent createDefault() {
        return Correspondent.builder()
            .correspondentName("name")
            .businessRegNumber("123456789")
            .correspondentCategory(CorrespondentCategory.HEALTH)
            .build();
    }

    public static Correspondent createWithAllSearchConditions(Owner owner) {
        return Correspondent.builder()
            .correspondentName("name")
            .businessRegNumber("123456789")
            .owner(owner)
            .correspondentCategory(CorrespondentCategory.HEALTH)
            .build();
    }

}
