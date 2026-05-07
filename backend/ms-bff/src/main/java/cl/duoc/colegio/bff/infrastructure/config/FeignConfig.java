package cl.duoc.colegio.bff.infrastructure.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * Configuración global de Feign.
 *
 * Propaga headers de seguridad desde el API Gateway hacia los MSs downstream:
 *   X-User-Id   → RUT del usuario autenticado
 *   X-User-Role → Rol del usuario autenticado
 *   X-User-Uuid → UUID interno del usuario
 *
 * Esto permite que MS-Usuario y MS-Académico apliquen
 * autorización fina basada en headers confiables.
 */
@Configuration
@Slf4j
public class FeignConfig {

    @Bean
    public RequestInterceptor securityHeaderInterceptor() {
        return (RequestTemplate template) -> {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) return;

            HttpServletRequest request = attributes.getRequest();

            propagateHeader(template, request, "X-User-Id");
            propagateHeader(template, request, "X-User-Role");
            propagateHeader(template, request, "X-User-Uuid");
        };
    }

    private void propagateHeader(RequestTemplate template, HttpServletRequest request, String headerName) {
        String value = request.getHeader(headerName);
        if (value != null && !value.isBlank()) {
            template.header(headerName, value);
            log.debug("[BFF-Feign] Header propagado: {} = {}", headerName, value);
        }
    }
}
