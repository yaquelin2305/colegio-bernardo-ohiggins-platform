package cl.duoc.colegio.usuario.application.strategy;

import cl.duoc.colegio.usuario.domain.model.Permisos;
import cl.duoc.colegio.usuario.domain.model.Usuario;

import java.util.List;
import java.util.Map;

/**
 * Strategy de Autorización para el rol ESTUDIANTE.
 *
 * PROTECCIÓN DE DATOS PROPIOS:
 * Un estudiante solo puede ver sus propios datos académicos.
 * El claim "estudianteId" es usado por el MS-Académico para filtrar
 * consultas en la base de datos.
 *
 * Permisos:
 * - Solo lectura de SUS notas, asistencias y datos académicos.
 * - NO puede modificar nada.
 * - NO puede ver datos de otros estudiantes.
 */
public class EstudianteAuthorizationStrategy implements AuthorizationStrategy {

    private static final List<String> RECURSOS = List.of(
            "notas",
            "asistencias"
    );

    private static final List<String> OPERACIONES = List.of("GET");

    @Override
    public Permisos resolverPermisos(Usuario usuario) {
        return new Permisos(RECURSOS, OPERACIONES, true);
    }

    @Override
    public Map<String, Object> generarClaimsAdicionales(Usuario usuario) {
        return Map.of(
                "rol", "ESTUDIANTE",
                "estudianteId", usuario.getPerfilId() != null ? usuario.getPerfilId() : 0L,
                "recursos", RECURSOS,
                "soloLectura", true
        );
    }
}
