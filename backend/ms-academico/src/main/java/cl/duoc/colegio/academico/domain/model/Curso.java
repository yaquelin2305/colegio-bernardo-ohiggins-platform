package cl.duoc.colegio.academico.domain.model;

import java.util.Objects;

/**
 * Entidad de dominio: Curso.
 * Un curso tiene un nombre, año escolar y un profesor jefe (UUID del docente en MS-Usuario).
 */
public class Curso {

    private final Long id;
    private final String nombre;
    private final Integer anioEscolar;
    private final java.util.UUID profesorJefeUuid;

    public Curso(Long id, String nombre, Integer anioEscolar, java.util.UUID profesorJefeUuid) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre, "Nombre del curso no puede ser nulo");
        this.anioEscolar = Objects.requireNonNull(anioEscolar, "Año escolar no puede ser nulo");
        this.profesorJefeUuid = profesorJefeUuid; // Opcional — puede no tener asignado aún
    }

    public Long getId()                           { return id; }
    public String getNombre()                      { return nombre; }
    public Integer getAnioEscolar()                { return anioEscolar; }
    public java.util.UUID getProfesorJefeUuid()    { return profesorJefeUuid; }
}
