package cl.duoc.colegio.academico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Punto de entrada del Microservicio de Gestión Académica.
 * Colegio Bernardo O'Higgins — Proyecto Fullstack III.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class MsAcademicoApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsAcademicoApplication.class, args);
    }
}
