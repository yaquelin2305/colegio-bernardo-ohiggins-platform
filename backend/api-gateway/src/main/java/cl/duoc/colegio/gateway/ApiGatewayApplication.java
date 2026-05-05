package cl.duoc.colegio.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway — Colegio Bernardo O'Higgins
 *
 * Punto de entrada único para todos los microservicios.
 * Responsabilidades:
 *  - Enrutamiento dinámico via Eureka (lb://)
 *  - Validación JWT por perfil (dev/prod)
 *  - Circuit Breaker con Resilience4j
 *  - Respuestas de fallback controladas (RFC 7807)
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
