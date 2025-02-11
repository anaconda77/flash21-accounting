package com.flash21.accounting.common;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountingException.class)
    public ResponseEntity<ErrorResponse> handleTalKakException(AccountingException e) {
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
}
