package com.flash21.accounting.common.exception.aop;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.ReflectionErrorCode;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ReflectionAspect {

    // 정확한 패키지 경로를 지정
    @Around("@annotation(com.flash21.accounting.common.exception.aop.ReflectionOperation)")
    public Object handleReflectionException(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                 IllegalArgumentException | ClassCastException | UndeclaredThrowableException |
                 IllegalStateException e) {
            throw AccountingException.of(ReflectionErrorCode.REFLECTION_ERROR, e);
        } catch (NullPointerException e) {
            throw AccountingException.of(ReflectionErrorCode.REFLECTION_UNFOUND_METHOD, e);
        }
    }
}
