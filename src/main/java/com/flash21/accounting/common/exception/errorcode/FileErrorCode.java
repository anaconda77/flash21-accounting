package com.flash21.accounting.common.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {
    FILE_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "F001", "파일 처리 도중 오류가 발생하였습니다."),
    UNSUPPORTED_OS(HttpStatus.BAD_REQUEST, "F002", "지원하지 않는 운영체제입니다."),
    UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, "F003", "지원하지 않는 확장자 타입입니다.");

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
