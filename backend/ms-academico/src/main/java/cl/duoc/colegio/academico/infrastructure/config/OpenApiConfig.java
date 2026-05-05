package cl.duoc.colegio.academico.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI / Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("MS Gestión Académica — API")
                        .description("Microservicio de gestión académica para el Colegio Bernardo O'Higgins. " +
                                "Maneja notas, asistencia y reportes académicos.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo Desarrollo Fullstack III")
                                .email("desarrollo@colegiobernardo.cl"))
                        .license(new License().name("Académico — Duoc UC")))
                .servers(List.of(
                        new Server().url("http://localhost:8082").description("Desarrollo local"),
                        new Server().url("https://ms-academico.railway.app").description("Producción Railway")
                ));
    }
}
