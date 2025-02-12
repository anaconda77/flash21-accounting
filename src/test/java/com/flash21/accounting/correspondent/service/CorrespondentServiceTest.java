package com.flash21.accounting.correspondent.service;

import static com.flash21.accounting.common.ErrorCodeAssertions.assertErrorCode;

import com.flash21.accounting.common.ErrorCodeAssertions;
import com.flash21.accounting.common.exception.errorcode.CorrespondentErrorCode;
import com.flash21.accounting.common.exception.errorcode.ReflectionErrorCode;
import com.flash21.accounting.correspondent.model.Correspondent;
import com.flash21.accounting.correspondent.repository.CorrespondentRepository;
import com.flash21.accounting.fixture.correspondent.CorrespondentFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=aabsfsdbd"
})
@AutoConfigureTestDatabase
@Transactional
class CorrespondentServiceTest {

    @Autowired
    private CorrespondentService correspondentService;
    @Autowired
    private CorrespondentRepository correspondentRepository;
    private Correspondent correspondent;

    @BeforeEach
    void setUp() {
        correspondent = correspondentRepository.save(CorrespondentFixture.createWithAllSearchConditions());
    }

    @DisplayName("올바른 검색 조건에 대한 조회 성공 테스트")
    @ParameterizedTest
    @CsvSource({
        ",",
        "correspondentName, name",
        "businessRegNumber, 123456789",
        "ownerName, owner",
    })
    void legalSearchInputs(String condition, String value) {
        Assertions.assertDoesNotThrow(() -> correspondentService.getCorrespondents(condition, value));
    }

    @DisplayName("잘못된 검색 조건에 대한 조회 실패 테스트")
    @ParameterizedTest
    @CsvSource({
        "correspondentname, name",
        "correspondentNam, name",
        "name, name"
    })
    void illegalSearchConditionInputs(String condition, String value) {
        assertErrorCode(
            ReflectionErrorCode.REFLECTION_UNFOUND_METHOD,() -> correspondentService.getCorrespondents(condition, value));
    }

    @DisplayName("잘못된 검색 값에 대한 조회 실패 테스트")
    @ParameterizedTest
    @CsvSource({
        "correspondentName,   ",
        "businessRegNumber,  ",
        "ownerName, "
    })
    void illegalSearchValueInputs(String condition, String value) {
        assertErrorCode(
            CorrespondentErrorCode.NOT_ALLOWING_EMPTY_SEARCH_VALUES,() -> correspondentService.getCorrespondents(condition, value));
    }


}