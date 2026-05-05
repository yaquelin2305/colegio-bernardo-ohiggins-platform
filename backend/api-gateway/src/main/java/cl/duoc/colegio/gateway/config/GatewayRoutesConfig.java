package cl.duoc.colegio.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de rutas adicionales del Gateway vía Java DSL.
 *
 * Las rutas principales están en application.yml.
 * Aquí se definen rutas especiales de monitoreo y actuator.
 *
 * Java DSL vs YAML:
 *  - YAML → ideal para rutas estándar simples (la mayoría)
 *  - Java DSL → ideal para rutas con lógica condicional o predicados complejos
 */
@Configuration
public class GatewayRoutesConfig {

    /**
     * Ruta de health check del propio Gateway.
     * Accesible sin autenticación para monitoring externo (Railway, etc.)
     */
    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                // Ruta de health/info expuesta públicamente
                .route("gateway-health", r -> r
                        .path("/health", "/info")
                        .filters(f -> f.rewritePath("/(?<segment>.*)",
                                "/actuator/${segment}"))
                        .uri("http://localhost:8080")
                )
                .build();
    }
}
