package com.flash21.accounting.common.interceptor;

import com.flash21.accounting.common.exception.AccountingException;
import com.flash21.accounting.common.exception.errorcode.FileErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Parameter;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class MultipartFileFieldNameValidationInterceptor implements HandlerInterceptor {
    private static final Set<String> ALLOWED_FIELD_NAMES = Set.of(
        "businessRegNumberImage",
        "bankBookImage",
        "json"
    );

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;

        // RequestPart 어노테이션이 붙은 파라미터들 검사
        Parameter[] parameters = handlerMethod.getMethod().getParameters();
        for (Parameter parameter : parameters) {
            RequestPart requestPart = parameter.getAnnotation(RequestPart.class);
            if (requestPart != null && !ALLOWED_FIELD_NAMES.contains(requestPart.value())) {
                throw AccountingException.of(FileErrorCode.UNSUPPORTED_FILE_FILED_NAME);
            }
        }

        return true;
    }
}
