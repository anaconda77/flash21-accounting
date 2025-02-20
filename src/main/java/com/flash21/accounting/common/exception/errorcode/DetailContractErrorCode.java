package com.flash21.accounting.common.exception.errorcode;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum DetailContractErrorCode implements ErrorCode {

    DETAIL_CONTRACT_NOT_FOUND(HttpStatus.NOT_FOUND, "DC001", "존재하지 않는 세부계약서입니다."),
    ALREADY_EXIST_DETAIL_CONTRACT(HttpStatus.BAD_REQUEST, "DC002", "이미 등록된 세부계약서입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "DC003", "필수 입력값이 누락되었습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "DC004", "서버 내부 오류가 발생했습니다."),
    CONTRACT_NOT_FOUND(HttpStatus.NOT_FOUND, "DC005", "존재하지 않는 상위계약서입니다."),

    //상태변경
    CANNOT_UPDATE_CANCELED_CONTRACT(HttpStatus.BAD_REQUEST, "DC006", "취소된 계약서는 수정할 수 없습니다."),
    CANNOT_UPDATE_DONE_CONTRACT(HttpStatus.BAD_REQUEST, "DC007", "완료된 계약서는 수정할 수 없습니다."),
    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "DC008", "잘못된 상태 변경입니다."),

    // 카테고리
    INVALID_CATEGORY(HttpStatus.NOT_FOUND, "DC009", "존재하지 않은 카테고리입니다."),
    INVALID_STATUS(HttpStatus.NOT_FOUND, "DC010", "존재하지 않은 상태입니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "DC011", "결제를 찾을 수 없습니다.");


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
