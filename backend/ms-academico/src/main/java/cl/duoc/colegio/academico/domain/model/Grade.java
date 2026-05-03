package cl.duoc.colegio.academico.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Entidad de dominio: Calificación académica.
 *
 * Modelo flexible: una instancia = una nota con tipo (PRUEBA, TAREA, EXAMEN, TRABAJO).
 * El GradeContractMapper agrupa N notas → {nota_1, nota_2, nota_3, promedio}
 * para satisfacer el contrato PUT /calificaciones/guardar.
 *
 * Escala chilena: 1.0 a 7.0
 */
public class Grade {

    private final Long id;
    private final UUID usuarioUuid;   // Referencia lógica al MS-Usuario
    private final Long asignaturaId;  // FK lógica a Asignatura
    private final double nota;
    private final String tipo;        // PRUEBA, TAREA, EXAMEN, TRABAJO
    private final String descripcion;

    public Grade(Long id, UUID usuarioUuid, Long asignaturaId,
                 double nota, String tipo, String descripcion) {
        this.id = id;
        this.usuarioUuid = Objects.requireNonNull(usuarioUuid, "usuarioUuid no puede ser nulo");
        this.asignaturaId = Objects.requireNonNull(asignaturaId, "asignaturaId no puede ser nulo");
        this.tipo = Objects.requireNonNull(tipo, "Tipo no puede ser nulo");
        this.descripcion = descripcion;
        this.nota = validarNota(nota);
    }

    // ── Lógica de negocio ──────────────────────────────────────────────────────

    private double validarNota(double nota) {
        if (nota < 1.0 || nota > 7.0) {
            throw new IllegalArgumentException(
                "Nota inválida: " + nota + ". Debe estar entre 1.0 y 7.0 (escala chilena)");
        }
        return nota;
    }

    public boolean esAprobatoria() {
        return nota >= 4.0;
    }

    // ── Getters ────────────────────────────────────────────────────────────────
    public Long getId()            { return id; }
    public UUID getUsuarioUuid()   { return usuarioUuid; }
    public Long getAsignaturaId()  { return asignaturaId; }
    public double getNota()        { return nota; }
    public String getTipo()        { return tipo; }
    public String getDescripcion() { return descripcion; }
}
