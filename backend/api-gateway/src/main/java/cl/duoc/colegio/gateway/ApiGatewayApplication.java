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
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
