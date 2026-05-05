package cl.duoc.colegio.usuario.application.strategy;

import cl.duoc.colegio.usuario.domain.model.Permisos;
import cl.duoc.colegio.usuario.domain.model.Usuario;

import java.util.List;
import java.util.Map;

/**
 * Strategy de Autorización para el rol DOCENTE.
 *
 * Permisos:
 * - Puede ver y modificar notas y asistencias de sus cursos asignados.
 * - Puede ver el listado de estudiantes de sus cursos.
 * - NO puede ver datos de otros docentes ni información financiera.
 * - NO puede crear/eliminar estudiantes (eso es admin).
 */
public class DocenteAuthorizationStrategy implements AuthorizationStrategy {

    private static final List<String> RECURSOS = List.of(
            "notas",
            "asistencias",
            "estudiantes",
            "cursos",
            "reportes-academicos"
    );

    private static final List<String> OPERACIONES = List.of(
            "GET",
            "POST",
            "PUT"
    );

    @Override
    public Permisos resolverPermisos(Usuario usuario) {
        return new Permisos(RECURSOS, OPERACIONES, false);
    }

    @Override
    public Map<String, Object> generarClaimsAdicionales(Usuario usuario) {
        return Map.of(
                "rol", "DOCENTE",
                "perfilId", usuario.getPerfilId() != null ? usuario.getPerfilId() : 0L,
                "recursos", RECURSOS,
                "soloLectura", false
        );
    }
}
