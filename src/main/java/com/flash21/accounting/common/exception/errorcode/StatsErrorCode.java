package com.flash21.accounting.common.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum StatsErrorCode implements ErrorCode {
    FAILED_CONVERTING_TO_JSON(HttpStatus.BAD_REQUEST, "S001", "JSON으로 변환이 실패하였습니다."),
    FAILED_PARSING_JSON_TO_OBJECT(HttpStatus.BAD_REQUEST, "S002", "JSON에서 객체로 변환이 실패하였습니다."),
    CANNOT_CALCULATE_STATS(HttpStatus.NOT_FOUND, "S003", "해당 조건에 부합하는 통계 데이터가 없습니다.");

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
