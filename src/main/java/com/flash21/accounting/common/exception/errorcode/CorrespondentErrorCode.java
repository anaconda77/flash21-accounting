package com.flash21.accounting.common.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum CorrespondentErrorCode implements ErrorCode {

    EXISTING_CORRESPONDENT(HttpStatus.BAD_REQUEST, "C001", "동일한 이름의 거래처가 존재합니다."),
    NOT_ALLOWING_EMPTY_SEARCH_VALUES(HttpStatus.BAD_REQUEST, "C002", "거래처 검색 조건의 값으로 빈 값을 입력할 수 없습니다.");


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
