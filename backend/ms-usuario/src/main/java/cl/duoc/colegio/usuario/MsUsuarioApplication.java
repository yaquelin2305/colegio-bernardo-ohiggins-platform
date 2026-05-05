package cl.duoc.colegio.usuario;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MS-Usuario — Microservicio de Autenticación y Autorización
 * Colegio Bernardo O'Higgins — Plataforma FS3 DUOC
 *
 * Puerto: 8083
 * Arquitectura: Hexagonal (Ports & Adapters)
 * Patrones: Strategy (Autorización), Factory Method (UserStrategyFactory)
 */
@SpringBootApplication
public class MsUsuarioApplication {
    public static void main(String[] args) {
        SpringApplication.run(MsUsuarioApplication.class, args);
    }
}
