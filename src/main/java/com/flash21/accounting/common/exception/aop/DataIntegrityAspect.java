package com.flash21.accounting.common.exception.aop;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.CommonErrorCode;
import com.flash21.accounting.common.exception.errorcode.CorrespondentErrorCode;
import jakarta.validation.ConstraintViolationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataIntegrityAspect {

    @Around("execution(* com.flash21.accounting.*.repository.*.save*(..))")
    public Object handleDataIntegrity(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (DataIntegrityViolationException e) {
            String message = e.getMessage();
            if (message.contains("UK_CORRESPONDENT_NAME")) {
                throw AccountingException.of(CorrespondentErrorCode.EXISTING_CORRESPONDENT, e);
            }

            throw AccountingException.of(CommonErrorCode.UNKNOWN_INTEGRITY, e);
        }
    }
}
