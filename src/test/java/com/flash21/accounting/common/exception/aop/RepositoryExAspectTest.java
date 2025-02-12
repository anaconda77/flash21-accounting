package com.flash21.accounting.common.exception.aop;

import static com.flash21.accounting.common.ErrorCodeAssertions.assertErrorCode;

import com.flash21.accounting.common.exception.errorcode.CorrespondentErrorCode;
import com.flash21.accounting.correspondent.domain.Correspondent;
import com.flash21.accounting.correspondent.repository.CorrespondentRepository;
import com.flash21.accounting.fixture.correspondent.CorrespondentFixture;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureTestDatabase
class RepositoryExAspectTest {

    Correspondent originalCorrespondent = CorrespondentFixture.createDefault();
    Correspondent dupliactedCorrespondent = CorrespondentFixture.createDefault();
    @Autowired
    CorrespondentRepository correspondentRepository;

    @Test
    void uniqueExceptionTest() {
        correspondentRepository.save(originalCorrespondent);
        assertErrorCode(CorrespondentErrorCode.EXISTING_CORRESPONDENT,
            () -> correspondentRepository.save(dupliactedCorrespondent));

    }
}