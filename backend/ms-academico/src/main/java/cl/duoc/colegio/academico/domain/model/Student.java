package cl.duoc.colegio.academico.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Entidad de dominio: Estudiante.
 * Pura lógica de negocio — sin anotaciones de framework.
 */
public class Student {

    private final Long id;
    private final String rut;
    private final String nombre;
    private final String apellido;
    private final Integer curso;
    private final List<Grade> grades;
    private final List<Attendance> attendances;

    public Student(Long id, String rut, String nombre, String apellido, Integer curso) {
        this.id = id;
        this.rut = Objects.requireNonNull(rut, "RUT no puede ser nulo");
        this.nombre = Objects.requireNonNull(nombre, "Nombre no puede ser nulo");
        this.apellido = Objects.requireNonNull(apellido, "Apellido no puede ser nulo");
        this.curso = Objects.requireNonNull(curso, "Curso no puede ser nulo");
        this.grades = new ArrayList<>();
        this.attendances = new ArrayList<>();
    }

    // ===== LÓGICA DE NEGOCIO =====

    /**
     * Calcula el promedio final del estudiante.
     * Retorna 0.0 si no tiene notas registradas.
     */
    public double calcularPromedio() {
        if (grades.isEmpty()) return 0.0;
        return grades.stream()
                .mapToDouble(Grade::getNota)
                .average()
                .orElse(0.0);
    }

    /**
     * Calcula el porcentaje de asistencia del estudiante.
     * Retorna 100.0 si no hay registros (sin datos = no se penaliza).
     */
    public double calcularPorcentajeAsistencia() {
        if (attendances.isEmpty()) return 100.0;
        long presentes = attendances.stream()
                .filter(Attendance::isPresente)
                .count();
        return (presentes * 100.0) / attendances.size();
    }

    /**
     * Determina si el estudiante está en riesgo de repitencia por inasistencia.
     * Regla de negocio: menos del 85% de asistencia = en riesgo.
     */
    public boolean estaEnRiesgoRepitenciaPorAsistencia() {
        return calcularPorcentajeAsistencia() < 85.0;
    }

    /**
     * Determina si el estudiante está reprobado académicamente.
     * Regla de negocio: promedio menor a 4.0 en escala chilena.
     */
    public boolean estaReprobado() {
        return calcularPromedio() < 4.0;
    }

    public void agregarNota(Grade grade) {
        Objects.requireNonNull(grade, "La nota no puede ser nula");
        this.grades.add(grade);
    }

    public void agregarAsistencia(Attendance attendance) {
        Objects.requireNonNull(attendance, "La asistencia no puede ser nula");
        this.attendances.add(attendance);
    }

    // ===== GETTERS =====
    public Long getId() { return id; }
    public String getRut() { return rut; }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public Integer getCurso() { return curso; }
    public List<Grade> getGrades() { return Collections.unmodifiableList(grades); }
    public List<Attendance> getAttendances() { return Collections.unmodifiableList(attendances); }
    public String getNombreCompleto() { return nombre + " " + apellido; }
}
