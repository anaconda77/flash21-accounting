package com.flash21.accounting.common.exception.aop;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.FileErrorCode;
import java.io.IOException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FileAspect {

    @Around("@annotation(com.flash21.accounting.common.*.aop.FileOperation)")
    public Object handleFileException(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (IOException e) {
            throw AccountingException.of(
                FileErrorCode.FILE_PROCESSING_ERROR, e);
        }
    }
}
