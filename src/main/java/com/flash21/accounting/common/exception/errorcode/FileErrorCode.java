package com.flash21.accounting.common.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum FileErrorCode implements ErrorCode {
    FILE_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "F001", "파일 처리 도중 오류가 발생하였습니다."),
    UNSUPPORTED_OS(HttpStatus.BAD_REQUEST, "F002", "지원하지 않는 운영체제입니다."),
    UNSUPPORTED_FILE_TYPE(HttpStatus.BAD_REQUEST, "F003", "지원하지 않는 확장자 타입입니다."),
    MISSING_ID(HttpStatus.BAD_REQUEST, "F004", "요청 대상 API Id 값 혹은 타입 Id 값이 누락되었습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "F005", "해당 API Id에 대한 유효하지 않은 다운로드 요청입니다. 파라미터 값을 다시 확인해주세요");

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
