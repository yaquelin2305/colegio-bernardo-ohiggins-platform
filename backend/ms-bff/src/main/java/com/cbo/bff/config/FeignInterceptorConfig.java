package com.cbo.bff.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;

@Configuration
public class FeignInterceptorConfig implements RequestInterceptor {

    private static final List<String> IDENTITY_HEADERS = List.of(
            "X-User-Uuid",
            "X-User-Id",
            "X-User-Role"
    );

    @Override
    public void apply(RequestTemplate template) {
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            return;
        }

        HttpServletRequest request = attributes.getRequest();
        for (String header : IDENTITY_HEADERS) {
            String value = request.getHeader(header);
            if (value != null) {
                template.header(header, value);
            }
        }
    }
}
