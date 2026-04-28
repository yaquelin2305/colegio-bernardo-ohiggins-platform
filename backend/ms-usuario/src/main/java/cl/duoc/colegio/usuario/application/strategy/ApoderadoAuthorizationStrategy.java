package cl.duoc.colegio.usuario.application.strategy;

import cl.duoc.colegio.usuario.domain.model.Permisos;
import cl.duoc.colegio.usuario.domain.model.Usuario;

import java.util.List;
import java.util.Map;

/**
 * Strategy de Autorización para el rol APODERADO.
 *
 * PROTECCIÓN DE DATOS DE MENORES:
 * Esta estrategia es crítica desde el punto de vista ético y legal.
 * Un apoderado SOLO puede ver datos de su pupilo registrado (perfilId).
 * El filtrado se aplica en el JWT claim "pupiloId" que el MS-Académico
 * usa para restringir las consultas a nivel de base de datos.
 *
 * Permisos:
 * - Solo lectura (NO puede modificar nada).
 * - Solo puede acceder a datos de su pupilo (filtrado por perfilId).
 * - NO puede ver datos de otros estudiantes bajo ninguna circunstancia.
 * - NO puede acceder a notas o asistencias de otros alumnos.
 */
public class ApoderadoAuthorizationStrategy implements AuthorizationStrategy {

    private static final List<String> RECURSOS = List.of(
            "notas",
            "asistencias",
            "reportes-academicos"
    );

    private static final List<String> OPERACIONES = List.of("GET");

    @Override
    public Permisos resolverPermisos(Usuario usuario) {
        return new Permisos(RECURSOS, OPERACIONES, true);
    }

    @Override
    public Map<String, Object> generarClaimsAdicionales(Usuario usuario) {
        // El claim "pupiloId" es la restricción principal de seguridad.
        // El MS-Académico filtra TODAS las consultas usando este claim.
        return Map.of(
                "rol", "APODERADO",
                "pupiloId", usuario.getPerfilId() != null ? usuario.getPerfilId() : 0L,
                "recursos", RECURSOS,
                "soloLectura", true
        );
    }
}
