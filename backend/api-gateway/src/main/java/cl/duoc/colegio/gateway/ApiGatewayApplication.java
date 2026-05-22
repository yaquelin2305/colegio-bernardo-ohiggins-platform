package cl.duoc.colegio.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * API Gateway — Colegio Bernardo O'Higgins.
 *
 * Punto de entrada único a la plataforma de microservicios.
 * Toda request del frontend pasa por aquí primero.
 *
 * <h3>Responsabilidades</h3>
 * <ul>
 *   <li>Enrutamiento: redirige requests a ms-usuario (:8083), ms-academico (:8082), etc.</li>
 *   <li>Seguridad: valida JWT (firma + expiración) y aplica RBAC por rol</li>
 *   <li>Self-access: estudiantes solo ven su propio boletín</li>
 *   <li>Propagación de identidad: inyecta headers X-User-* a los MS downstream</li>
 *   <li>CORS centralizado: un solo punto de configuración para todo el sistema</li>
 * </ul>
 *
 * <h3>Perfiles</h3>
 * <ul>
 *   <li>{@code dev}  — sin seguridad JWT, rutas a localhost, sin Eureka</li>
 *   <li>{@code prod} — JWT + RBAC completo, rutas a contenedores Docker</li>
 * </ul>
 *
 * <h3>Stack técnico</h3>
 * Spring Cloud Gateway (WebFlux/Netty) — reactivo, no bloqueante.
 * {@code @EnableDiscoveryClient} para Eureka (solo perfil default, dev y prod lo desactivan).
 */
@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
