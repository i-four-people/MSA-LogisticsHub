package com.logistics.order.presentation.auth;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AuthHeaderArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @AuthHeader 어노테이션이 있는 경우 지원
        return parameter.hasParameterAnnotation(AuthHeader.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        // ThreadLocal에서 AuthHeaderInfo 가져오기
        AuthHeaderInfo authHeaderInfo = AuthContext.get();
        if (authHeaderInfo == null) {
            throw new IllegalStateException("AuthHeaderInfo is missing. Check AOP configuration.");
        }
        return authHeaderInfo;
    }
}
