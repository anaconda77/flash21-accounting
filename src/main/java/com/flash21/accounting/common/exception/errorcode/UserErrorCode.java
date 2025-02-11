package com.flash21.accounting.common.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "존재하지 않는 회원입니다."),
    PASSWORD_MISMATCH(HttpStatus.UNAUTHORIZED, "U002", "비밀번호가 일치하지 않습니다."),
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "U003", "중복된 아이디의 회원이 존재합니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public HttpStatus status() {
        return status;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }
}
