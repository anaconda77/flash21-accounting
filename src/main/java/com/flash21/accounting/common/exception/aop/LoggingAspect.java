package com.flash21.accounting.common.exception.aop;

import com.flash21.accounting.common.exception.AccountingException;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(* com.flash21.accounting..controller..*(..))")
    public void controllerPointCut() {
    }

    @Pointcut("execution(* com.flash21.accounting..service..*(..))")
    public void servicePointCut() {
    }

    @Pointcut("execution(* com.flash21.accounting..model..*(..))")
    public void modelPointCut() {
    }

    @Around("controllerPointCut()")
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = null;
        long start = System.currentTimeMillis();
        log.debug("Enter: {}.{}() with argument[s] = {}", joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(), Arrays.toString(joinPoint.getArgs()));
        try {
            result = joinPoint.proceed();
        } finally {
            long end = System.currentTimeMillis();
            log.debug("Exit : {}.{}() with result = {} ({}ms)", joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(), result, end - start);
        }
        return result;
    }

    @AfterThrowing(pointcut = "servicePointCut() || modelPointCut()", throwing = "e")
    public void logException(JoinPoint joinPoint, AccountingException e) {
        log.warn("Exception in {}.{}() with cause: {}", joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(), e.getMessage(), e);
    }
}
