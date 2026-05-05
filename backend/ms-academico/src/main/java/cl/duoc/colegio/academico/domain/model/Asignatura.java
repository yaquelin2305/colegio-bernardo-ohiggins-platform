package cl.duoc.colegio.academico.domain.model;

import java.util.Objects;

/**
 * Entidad de dominio: Asignatura.
 * Materia que se dicta en el colegio (Matemáticas, Lenguaje, Historia, etc.)
 */
public class Asignatura {

    private final Long id;
    private final String nombre;
    private final Integer horasSemanales;

    public Asignatura(Long id, String nombre, Integer horasSemanales) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre, "Nombre de asignatura no puede ser nulo");
        this.horasSemanales = Objects.requireNonNull(horasSemanales, "Horas semanales no puede ser nulo");
    }

    public Long getId()               { return id; }
    public String getNombre()          { return nombre; }
    public Integer getHorasSemanales() { return horasSemanales; }
}
