package cl.duoc.colegio.usuario.domain.port.in;

import cl.duoc.colegio.usuario.domain.dto.AuthResponseDto;
import cl.duoc.colegio.usuario.domain.exception.EmailYaRegistradoException;
import cl.duoc.colegio.usuario.domain.model.RolUsuario;

import java.util.UUID;

/**
 * Puerto de entrada (driving port) — caso de uso Registro.
 *
 * Permite crear nuevos usuarios en la plataforma. Valida que no exista
 * duplicidad de RUT o email, hashea la contraseña y retorna JWT + permisos.
 *
 * <h3>Flujo</h3>
 * <ol>
 *   <li>Verifica que RUT y email no estén registrados → si existen, lanza {@link EmailYaRegistradoException}</li>
 *   <li>Hashea la contraseña con BCrypt (strength 12)</li>
 *   <li>Crea la entidad de dominio {@code Usuario} (activo por defecto)</li>
 *   <li>Asocia perfilId y pupiloUuid si corresponde (docente/apoderado)</li>
 *   <li>Persiste y retorna JWT con permisos del rol</li>
 * </ol>
 *
 * @param pupiloUuid solo usado para rol {@code APODERADO} — vincula al estudiante pupilo
 * @param perfilId   ID del perfil asociado (ej: id del docente en MS-Académico)
 */
public interface RegistroUseCase {
    AuthResponseDto registrar(String rut, String email, String password,
                              String nombre, String apellido, RolUsuario rol,
                              Long perfilId, UUID pupiloUuid);
}
