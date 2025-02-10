package com.flash21.accounting.common;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.ErrorCode;
import org.assertj.core.api.ThrowableAssert;

public class ErrorCodeAssertions {

    public static void assertErrorCode(ErrorCode errorCode, ThrowableAssert.ThrowingCallable callable) {
        assertThatThrownBy(callable)
            .isInstanceOf(AccountingException.class)
            .extracting("errorCode")
            .isEqualTo(errorCode);
    }
}
