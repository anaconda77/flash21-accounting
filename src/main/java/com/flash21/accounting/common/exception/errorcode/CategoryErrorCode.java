package com.flash21.accounting.common.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CategoryErrorCode implements ErrorCode {

    EXISTING_CATEGORY(HttpStatus.BAD_REQUEST, "CC001", "동일한 이름의 카테고리가 존재합니다."),
    NOT_FOUND_CATEGORY(HttpStatus.NOT_FOUND, "CC002", "해당 카테고리를 찾을 수 없습니다.");

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
