package com.flash21.accounting.common.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ReflectionErrorCode implements ErrorCode {

    REFLECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "RF001", "리플렉션 처리 도중 오류가 발생하였습니다."),
    REFLECTION_UNFOUND_METHOD(HttpStatus.INTERNAL_SERVER_ERROR, "RF002", "리플렉션 대상 메서드를 찾을 수 없습니다.");

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
