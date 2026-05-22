package cl.duoc.colegio.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de rutas adicionales via Java DSL.
 *
 * Las rutas principales están en {@code application.yml} (YAML).
 * Aquí se definen rutas especiales que requieren lógica programática.
 *
 * <h3>Rutas definidas aquí</h3>
 * <ul>
 *   <li>{@code GET /health} → redirige a {@code /actuator/health}</li>
 *   <li>{@code GET /info}   → redirige a {@code /actuator/info}</li>
 * </ul>
 *
 * <h3>¿Por qué Java DSL y no YAML?</h3>
 * El {@code rewritePath} necesita una expresión regular que en YAML
 * requiere escape doble (más propenso a errores). En Java es más legible.
 *
 * <h3>¿Por qué localhost:8080?</h3>
 * Estas rutas redirigen al propio Gateway (loopback), no a un MS externo.
 * El actuator del Gateway responde en el mismo proceso en :8080.
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
