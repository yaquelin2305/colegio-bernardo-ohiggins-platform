package cl.duoc.colegio.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * MS-BFF — Backend for Frontend / Orquestador.
 *
 * Responsabilidades:
 *  - Orquestar llamadas entre MS-Usuario y MS-Académico via Feign
 *  - Exponer endpoints unificados al frontend (dashboard, boletín)
 *  - NO tiene base de datos propia
 *  - Valida JWT propagado desde el API Gateway (via headers X-User-Id / X-User-Role)
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class MsBffApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsBffApplication.class, args);
    }
}
