package com.logistics.delivery.infrastructure.config;

import com.logistics.delivery.presentation.auth.AuthContext;
import com.logistics.delivery.presentation.auth.AuthHeaderInfo;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            AuthHeaderInfo authHeaderInfo = AuthContext.get();
            if (authHeaderInfo != null) {
                template.header("X-USER-ID", String.valueOf(authHeaderInfo.userId()));
                template.header("X-USER-ROLE", authHeaderInfo.role());
                template.header("X-User-Id", String.valueOf(authHeaderInfo.userId()));
                template.header("X-User-Role", authHeaderInfo.role());
            }
        };
    }
}
