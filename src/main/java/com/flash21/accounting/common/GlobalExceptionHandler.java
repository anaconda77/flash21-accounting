package com.flash21.accounting.common;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.ErrorResponse;
import com.flash21.accounting.common.exception.errorcode.ContractErrorCode;
import com.flash21.accounting.common.exception.errorcode.FileErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountingException.class)
    public ResponseEntity<ErrorResponse> handleAccountingException(AccountingException e) {
        log.error("=== Accounting Exception ===");
        log.error("Status: {}, Code: {}, Message: {}", e.status(), e.code(), e.message());
        log.error("Stack trace:", e);

        return ResponseEntity.status(e.status())
            .body(ErrorResponse.of(e.status(), e.code(), e.message()));
    }

    // Validation 과정에서 발생한 에러들을 잡아주기 위함
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("=== Validation Exception ===");
        log.error("Method: {}", e.getParameter().getExecutable());

        e.getBindingResult().getFieldErrors().forEach(error ->
            log.error("Field: {}, Value: {}, Message: {}",
                error.getField(),
                error.getRejectedValue(),
                error.getDefaultMessage())
        );
        log.error("Stack trace:", e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, "000",
                e.getBindingResult().getAllErrors().getFirst().getDefaultMessage()));
    }

    // Contract 내 Enum 값 받는 과정 HttpMessageNotReadableException 잡기위함
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error("JSON 변환 오류 발생 - Message: {}", e.getMessage());

        ContractErrorCode errorCode = ContractErrorCode.INVALID_ENUM;
        if (e.getMessage().contains("com.flash21.accounting.contract.entity.Status")) {
            errorCode = ContractErrorCode.INVALID_STATUS;
        } else if (e.getMessage().contains("com.flash21.accounting.contract.entity.ProcessStatus")) {
            errorCode = ContractErrorCode.INVALID_PROCESS_STATUS;
        }

        return ResponseEntity.status(errorCode.status())
                .body(ErrorResponse.of(errorCode.status(), errorCode.code(), errorCode.message()));
    }

    // 첨부파일 크기 초과
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("=== Max Upload Size Exceeded Exception ===");
        log.error("Max Upload Size Exceeded: {}", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse.of(HttpStatus.BAD_REQUEST, FileErrorCode.EXCEEDED_ALLOWING_FILE_SIZE.code(), FileErrorCode.EXCEEDED_ALLOWING_FILE_SIZE.message()));
    }
}
