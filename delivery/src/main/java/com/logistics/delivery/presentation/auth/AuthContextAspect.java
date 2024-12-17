package com.logistics.delivery.presentation.auth;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuthContextAspect {

    @Around("@annotation(org.springframework.amqp.rabbit.annotation.RabbitListener)")
    public Object manageAuthContext(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            // 메시지 리스너 실행 전
            return joinPoint.proceed();
        } finally {
            // AuthContext 클리어
            AuthContext.clear();
        }
    }
}
