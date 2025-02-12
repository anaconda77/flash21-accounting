package com.flash21.accounting.common.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum SignErrorCode implements ErrorCode {

    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "SE001", "서명할 사용자를 찾을 수 없습니다."),
    SIGN_NOT_FOUND(HttpStatus.NOT_FOUND, "SE002", "해당 서명을 찾을 수 없습니다."),
    SIGN_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "SE003", "이미 존재하는 서명입니다."),
    INVALID_SIGN_TYPE(HttpStatus.BAD_REQUEST, "SE004", "잘못된 서명 유형입니다."),
    SIGN_CREATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SE005", "서명 생성에 실패했습니다."),
    SIGN_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SE006", "서명 수정에 실패했습니다."),
    SIGN_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "SE007", "서명 삭제에 실패했습니다.");

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
