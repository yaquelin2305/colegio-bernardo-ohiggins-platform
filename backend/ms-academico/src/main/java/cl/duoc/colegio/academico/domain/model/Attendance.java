package cl.duoc.colegio.academico.domain.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad de dominio: Registro de Asistencia.
 */
public class Attendance {

    private final Long id;
    private final Long studentId;
    private final String asignatura;
    private final LocalDate fecha;
    private final boolean presente;
    private final String justificacion;  // nullable — solo aplica si ausente

    public Attendance(Long id, Long studentId, String asignatura,
                      LocalDate fecha, boolean presente, String justificacion) {
        this.id = id;
        this.studentId = Objects.requireNonNull(studentId, "studentId no puede ser nulo");
        this.asignatura = Objects.requireNonNull(asignatura, "Asignatura no puede ser nula");
        this.fecha = Objects.requireNonNull(fecha, "Fecha no puede ser nula");
        this.presente = presente;
        // Justificación solo tiene sentido si el alumno está ausente
        this.justificacion = (!presente) ? justificacion : null;
    }

    public boolean estaJustificado() {
        return !presente && justificacion != null && !justificacion.isBlank();
    }

    // ===== GETTERS =====
    public Long getId() { return id; }
    public Long getStudentId() { return studentId; }
    public String getAsignatura() { return asignatura; }
    public LocalDate getFecha() { return fecha; }
    public boolean isPresente() { return presente; }
    public String getJustificacion() { return justificacion; }
}
