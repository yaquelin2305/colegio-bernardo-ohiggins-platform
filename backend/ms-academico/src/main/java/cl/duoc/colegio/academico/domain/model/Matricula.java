package cl.duoc.colegio.academico.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Entidad de dominio: Matrícula.
 * Relación M:N entre un estudiante (por UUID) y un curso.
 */
public class Matricula {

    private final Long id;
    private final UUID usuarioUuid;   // FK lógica al estudiante en MS-Usuario
    private final Long cursoId;       // FK a academico_schema.cursos

    public Matricula(Long id, UUID usuarioUuid, Long cursoId) {
        this.id = id;
        this.usuarioUuid = Objects.requireNonNull(usuarioUuid, "usuarioUuid no puede ser nulo");
        this.cursoId = Objects.requireNonNull(cursoId, "cursoId no puede ser nulo");
    }

    public Long getId()          { return id; }
    public UUID getUsuarioUuid() { return usuarioUuid; }
    public Long getCursoId()     { return cursoId; }
}
