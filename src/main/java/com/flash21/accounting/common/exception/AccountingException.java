package com.flash21.accounting.common.exception;

import com.flash21.accounting.common.exception.errorcode.ErrorCode;
import org.springframework.http.HttpStatus;

public class AccountingException extends RuntimeException {
    private final ErrorCode errorCode;

    public AccountingException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

    public AccountingException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.message(), cause);
        this.errorCode = errorCode;
    }

    public static AccountingException of(ErrorCode errorCode) {
        return new AccountingException(errorCode);
    }

    public static AccountingException of(ErrorCode errorCode, Throwable cause) {
        return new AccountingException(errorCode, cause);
    }

    public HttpStatus status() {
        return errorCode.status();
    }

    public String code() {
        return errorCode.code();
    }

    public String message() {
        return errorCode.message();
    }
}
