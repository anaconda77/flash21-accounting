package com.flash21.accounting.common.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {

    UNKNOWN_DATA_ERROR(HttpStatus.BAD_REQUEST, "000", "알 수 없는 데이터 오류가 발생하였습니다."),
    EXISTING_DATA_ERROR(HttpStatus.BAD_REQUEST, "001", "해당 데이터가 이미 존재합니다."),
    NOT_FOUND_DATA_ERROR(HttpStatus.NOT_FOUND, "002", "존재하지 않는 데이터입니다.");

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
