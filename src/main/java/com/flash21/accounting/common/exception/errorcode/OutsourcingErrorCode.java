package com.flash21.accounting.common.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum OutsourcingErrorCode implements ErrorCode {
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "OS001", "존재하지 않는 상태입니다."),
    OUTSOURCING_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "OS002", "이미 존재하는 외주계약입니다."),
    OUTSOURCING_NOT_FOUND(HttpStatus.NOT_FOUND, "OS003","존재하지 않는 외주계약입니다."),
    //상태변경
    CANNOT_UPDATE_CANCELED_CONTRACT(HttpStatus.BAD_REQUEST, "OS004", "취소된 계약서는 수정할 수 없습니다."),
    CANNOT_UPDATE_DONE_CONTRACT(HttpStatus.BAD_REQUEST, "OS005", "완료된 계약서는 수정할 수 없습니다."),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "OS006", "잘못된 상태 변경입니다.");

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
