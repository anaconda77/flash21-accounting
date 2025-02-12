package com.flash21.accounting.common.exception.aop;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.CommonErrorCode;
import com.flash21.accounting.common.exception.errorcode.ReflectionErrorCode;
import java.lang.reflect.InvocationTargetException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;

public class ReflectionAspect {

    @Around("@annotation(com.example.correspondent.annotation.ReflectionOperation)")
    public Object handleReflectionException(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (NoSuchMethodException e) {
            // 메소드를 찾을 수 없을 때
            throw AccountingException.of(ReflectionErrorCode.REFLECTION_ERROR, e);

        } catch (IllegalAccessException e) {
            // 메소드에 접근할 권한이 없을 때
            throw AccountingException.of(ReflectionErrorCode.REFLECTION_ERROR, e);

        } catch (InvocationTargetException e) {
            // 호출된 메소드 내부에서 예외가 발생했을 때
            throw AccountingException.of(ReflectionErrorCode.REFLECTION_ERROR, e);

        } catch (IllegalArgumentException e) {
            // 잘못된 인자가 전달되었을 때
            throw AccountingException.of(ReflectionErrorCode.REFLECTION_ERROR, e);

        } catch (ClassCastException e) {
            // 반환값 타입 캐스팅 실패시
            throw AccountingException.of(ReflectionErrorCode.REFLECTION_ERROR, e);
        }
    }
}
