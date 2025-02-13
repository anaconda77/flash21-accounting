package com.flash21.accounting.common.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum ContractErrorCode implements ErrorCode {
    // 계약서 관련 에러 코드
    CONTRACT_NOT_FOUND(HttpStatus.NOT_FOUND, "CE001", "요청한 계약서를 찾을 수 없습니다."),
    USER_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "CE002", "해당 작업을 수행할 권한이 없습니다."),
    USER_FORBIDDEN(HttpStatus.FORBIDDEN, "CE003", "계약서를 수정할 권한이 없습니다."),

    // 필수 입력값 누락
    REQUIRED_FIELD_MISSING(HttpStatus.BAD_REQUEST, "CE004", "필수 입력 항목이 누락되었습니다."),

    // 잘못된 입력값
    INVALID_CONTRACT_NAME(HttpStatus.BAD_REQUEST, "CE005", "유효하지 않은 계약서 명입니다."),

    // 관리자 권한 관련
    ONLY_SUPER_ADMIN_ALLOWED(HttpStatus.FORBIDDEN, "CE006", "해당 작업은 슈퍼관리자만 수행할 수 있습니다."),

    // Enum 변환 실패 관련 에러
    INVALID_ENUM(HttpStatus.BAD_REQUEST, "CE007", "유효하지 않은 Enum 값입니다."),
    INVALID_METHOD(HttpStatus.BAD_REQUEST, "CE08", "유효하지 않은 계약방식(Method) 값입니다."),
    INVALID_STATUS(HttpStatus.BAD_REQUEST, "CE09", "유효하지 않은 계약 상태(Status) 값입니다."),
    INVALID_PROCESS_STATUS(HttpStatus.BAD_REQUEST, "CE010", "유효하지 않은 진행 상태(ProcessStatus) 값입니다.");

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
