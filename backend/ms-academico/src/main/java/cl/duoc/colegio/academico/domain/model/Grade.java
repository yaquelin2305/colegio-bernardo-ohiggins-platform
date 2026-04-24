package cl.duoc.colegio.academico.domain.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entidad de dominio: Nota académica.
 * Escala chilena: 1.0 a 7.0
 */
public class Grade {

    private final Long id;
    private final Long studentId;
    private final String asignatura;
    private final double nota;
    private final String tipo;       // PRUEBA, TAREA, EXAMEN, TRABAJO
    private final LocalDate fecha;
    private final String descripcion;

    public Grade(Long id, Long studentId, String asignatura, double nota,
                 String tipo, LocalDate fecha, String descripcion) {
        this.id = id;
        this.studentId = Objects.requireNonNull(studentId, "studentId no puede ser nulo");
        this.asignatura = Objects.requireNonNull(asignatura, "Asignatura no puede ser nula");
        this.tipo = Objects.requireNonNull(tipo, "Tipo no puede ser nulo");
        this.fecha = Objects.requireNonNull(fecha, "Fecha no puede ser nula");
        this.descripcion = descripcion;
        this.nota = validarNota(nota);
    }

    // ===== LÓGICA DE NEGOCIO =====

    private double validarNota(double nota) {
        if (nota < 1.0 || nota > 7.0) {
            throw new IllegalArgumentException(
                "Nota inválida: " + nota + ". Debe estar entre 1.0 y 7.0");
        }
        return nota;
    }

    public boolean esAprobatoria() {
        return nota >= 4.0;
    }

    // ===== GETTERS =====
    public Long getId() { return id; }
    public Long getStudentId() { return studentId; }
    public String getAsignatura() { return asignatura; }
    public double getNota() { return nota; }
    public String getTipo() { return tipo; }
    public LocalDate getFecha() { return fecha; }
    public String getDescripcion() { return descripcion; }
}
