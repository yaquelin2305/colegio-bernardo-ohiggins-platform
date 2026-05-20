package cl.duoc.colegio.academico.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object de dominio: contrato de calificaciones (nota1, nota2, nota3, promedio).
 *
 * Agrupa las 3 notas fijas del ERD en un solo objeto para los endpoints
 * que operan sobre el contrato PUT /calificaciones/guardar.
 * Complementa a Grade, que maneja una nota individual por registro.
 */
public class GradeContract {

    private final UUID usuarioUuid;
    private final Long asignaturaId;
    private final Double nota1;
    private final Double nota2;
    private final Double nota3;
    private final Double promedio;

    public GradeContract(UUID usuarioUuid, Long asignaturaId,
                         Double nota1, Double nota2, Double nota3, Double promedio) {
        this.usuarioUuid = Objects.requireNonNull(usuarioUuid, "usuarioUuid no puede ser nulo");
        this.asignaturaId = Objects.requireNonNull(asignaturaId, "asignaturaId no puede ser nulo");
        this.nota1 = nota1;
        this.nota2 = nota2;
        this.nota3 = nota3;
        this.promedio = Objects.requireNonNull(promedio, "promedio no puede ser nulo");
    }

    public UUID getUsuarioUuid()   { return usuarioUuid; }
    public Long getAsignaturaId()  { return asignaturaId; }
    public Double getNota1()       { return nota1; }
    public Double getNota2()       { return nota2; }
    public Double getNota3()       { return nota3; }
    public Double getPromedio()    { return promedio; }
}
