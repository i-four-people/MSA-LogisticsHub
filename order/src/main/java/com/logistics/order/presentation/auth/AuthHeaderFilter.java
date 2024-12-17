package com.logistics.order.presentation.auth;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AuthHeaderFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String userId = httpRequest.getHeader("X-USER-ID");
        String role = httpRequest.getHeader("X-USER-ROLE");

        if (userId != null && role != null) {
            // ThreadLocal에 저장
            AuthContext.set(new AuthHeaderInfo(Long.valueOf(userId), role));
        }

        // 다음 필터 또는 컨트롤러 호출
        chain.doFilter(request, response);

        // ThreadLocal 초기화
        AuthContext.clear();
    }
}
