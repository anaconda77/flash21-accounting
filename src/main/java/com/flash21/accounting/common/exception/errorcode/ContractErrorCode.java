package com.flash21.accounting.common.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ContractErrorCode implements ErrorCode {

    // 계약서 관련 에러 코드
    CONTRACT_NOT_FOUND(HttpStatus.NOT_FOUND, "CT001", "요청한 계약서를 찾을 수 없습니다."),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "CT002", "해당 작업을 수행할 권한이 없습니다."),
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, "CT003", "계약서를 수정할 권한이 없습니다."),

    // 필수 입력값 누락
    REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST, "CT004", "필수 입력 항목이 누락되었습니다."),

    // 잘못된 입력값
    INVALID_CONTRACT_NAME(HttpStatus.BAD_REQUEST, "CT005", "유효하지 않은 계약서 명입니다."),

    // 관리자 권한 관련
    ONLY_SUPER_ADMIN_ALLOWED(HttpStatus.FORBIDDEN, "CT006", "해당 작업은 슈퍼관리자만 수행할 수 있습니다."),

    // 서명 관련 에러

    INVALID_SIGN(HttpStatus.BAD_REQUEST, "CT007", "유효하지 않은 서명 ID입니다."),
    SIGN_NOT_FOUND(HttpStatus.NOT_FOUND, "CT008", "해당 서명을 찾을 수 없습니다.");

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
