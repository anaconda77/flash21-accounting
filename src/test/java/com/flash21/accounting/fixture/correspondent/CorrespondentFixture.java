package com.flash21.accounting.fixture.correspondent;

import com.flash21.accounting.correspondent.model.Correspondent;

public class CorrespondentFixture {
    public static Correspondent createDefault() {
        return Correspondent.builder()
            .correspondentName("name")
            .businessRegNumber("123456789")
            .build();
    }

}
