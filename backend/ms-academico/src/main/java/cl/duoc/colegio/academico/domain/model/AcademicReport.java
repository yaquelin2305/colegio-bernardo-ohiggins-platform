package cl.duoc.colegio.academico.domain.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Value Object de dominio: Reporte académico de un estudiante.
 * Generado por el Factory Method — inmutable.
 */
public class AcademicReport {

    public enum TipoAlerta {
        SIN_ALERTA,
        ALERTA_RENDIMIENTO,       // Promedio < 4.0
        ALERTA_ASISTENCIA,        // Asistencia < 85%
        ALERTA_CRITICA            // Ambas condiciones
    }

    private final Long studentId;
    private final String nombreEstudiante;
    private final String curso;
    private final double promedio;
    private final double porcentajeAsistencia;
    private final TipoAlerta alerta;
    private final String mensajeAlerta;
    private final LocalDate fechaGeneracion;
    private final List<String> asignaturasReprobadas;

    public AcademicReport(Long studentId, String nombreEstudiante, String curso,
                          double promedio, double porcentajeAsistencia,
                          TipoAlerta alerta, String mensajeAlerta,
                          List<String> asignaturasReprobadas) {
        this.studentId = Objects.requireNonNull(studentId);
        this.nombreEstudiante = Objects.requireNonNull(nombreEstudiante);
        this.curso = Objects.requireNonNull(curso);
        this.promedio = promedio;
        this.porcentajeAsistencia = porcentajeAsistencia;
        this.alerta = Objects.requireNonNull(alerta);
        this.mensajeAlerta = mensajeAlerta;
        this.asignaturasReprobadas = List.copyOf(
            Objects.requireNonNullElse(asignaturasReprobadas, List.of()));
        this.fechaGeneracion = LocalDate.now();
    }

    public boolean tieneAlerta() {
        return alerta != TipoAlerta.SIN_ALERTA;
    }

    // ===== GETTERS =====
    public Long getStudentId() { return studentId; }
    public String getNombreEstudiante() { return nombreEstudiante; }
    public String getCurso() { return curso; }
    public double getPromedio() { return promedio; }
    public double getPorcentajeAsistencia() { return porcentajeAsistencia; }
    public TipoAlerta getAlerta() { return alerta; }
    public String getMensajeAlerta() { return mensajeAlerta; }
    public LocalDate getFechaGeneracion() { return fechaGeneracion; }
    public List<String> getAsignaturasReprobadas() { return asignaturasReprobadas; }
}
