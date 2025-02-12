package com.flash21.accounting.common.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum DetailContractErrorCode implements ErrorCode {

    DETAIL_CONTRACT_NOT_FOUND(HttpStatus.NOT_FOUND, "DC001", "존재하지 않는 세부계약서입니다."),
    ALREADY_EXIST_DETAIL_CONTRACT(HttpStatus.BAD_REQUEST, "DC002", "이미 등록된 세부계약서입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "DC003", "필수 입력값이 누락되었습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DC004", "서버 내부 오류가 발생했습니다.");

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
