package cl.duoc.colegio.usuario.application.strategy;

import cl.duoc.colegio.usuario.domain.model.Permisos;
import cl.duoc.colegio.usuario.domain.model.Usuario;

import java.util.List;
import java.util.Map;

/**
 * Strategy de Autorización para el rol ADMIN.
 *
 * Acceso total al sistema.
 * Solo debe asignarse a personal administrativo del colegio.
 */
public class AdminAuthorizationStrategy implements AuthorizationStrategy {

    private static final List<String> RECURSOS = List.of(
            "notas", "asistencias", "estudiantes", "docentes",
            "apoderados", "cursos", "reportes-academicos",
            "usuarios", "configuracion"
    );

    private static final List<String> OPERACIONES = List.of(
            "GET", "POST", "PUT", "DELETE"
    );

    @Override
    public Permisos resolverPermisos(Usuario usuario) {
        return new Permisos(RECURSOS, OPERACIONES, false);
    }

    @Override
    public Map<String, Object> generarClaimsAdicionales(Usuario usuario) {
        return Map.of(
                "rol", "ADMIN",
                "recursos", RECURSOS,
                "soloLectura", false
        );
    }
}
