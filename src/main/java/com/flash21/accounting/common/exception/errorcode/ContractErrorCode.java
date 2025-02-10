package com.flash21.accounting.common.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ContractErrorCode implements ErrorCode {
    NOT_FOUND(HttpStatus.NOT_FOUND, "CONTRACT_001", "존재하지 않는 계약서입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "CONTRACT_002", "인증되지 않은 유저입니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "CONTRACT_003", "수정할 권한이 없습니다."),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "CONTRACT_004", "는 필수 입력 항목입니다."),
    INVALID_PROJECT_NAME(HttpStatus.BAD_REQUEST, "CONTRACT_005", "프로젝트 명이 올바르지 않습니다."),
    SUPER_ADMIN_ONLY(HttpStatus.FORBIDDEN, "CONTRACT_006", "슈퍼관리자만 삭제가 가능합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public HttpStatus status() {
        return httpStatus;
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
