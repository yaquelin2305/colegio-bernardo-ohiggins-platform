package cl.duoc.colegio.academico.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Entidad de dominio: Asignación Docente.
 * Vincula un docente (UUID) a un curso + asignatura específica.
 */
public class AsignacionDocente {

    private final Long id;
    private final UUID docenteUuid;   // FK lógica al docente en MS-Usuario
    private final Long cursoId;       // FK a academico_schema.cursos
    private final Long asignaturaId;  // FK a academico_schema.asignaturas

    public AsignacionDocente(Long id, UUID docenteUuid, Long cursoId, Long asignaturaId) {
        this.id = id;
        this.docenteUuid = Objects.requireNonNull(docenteUuid, "docenteUuid no puede ser nulo");
        this.cursoId = Objects.requireNonNull(cursoId, "cursoId no puede ser nulo");
        this.asignaturaId = Objects.requireNonNull(asignaturaId, "asignaturaId no puede ser nulo");
    }

    public Long getId()           { return id; }
    public UUID getDocenteUuid()  { return docenteUuid; }
    public Long getCursoId()      { return cursoId; }
    public Long getAsignaturaId() { return asignaturaId; }
}
