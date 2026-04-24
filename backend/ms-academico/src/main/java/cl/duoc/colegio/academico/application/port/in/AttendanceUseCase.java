package cl.duoc.colegio.academico.application.port.in;

import cl.duoc.colegio.academico.domain.model.Attendance;
import java.time.LocalDate;
import java.util.List;

/**
 * Puerto de entrada — Casos de uso de Asistencia.
 */
public interface AttendanceUseCase {

    Attendance registrarAsistencia(Attendance attendance);

    Attendance obtenerAsistenciaPorId(Long id);

    List<Attendance> listarAsistenciasPorEstudiante(Long studentId);

    List<Attendance> listarAsistenciasPorEstudianteYFecha(Long studentId, LocalDate fecha);

    double calcularPorcentajeAsistencia(Long studentId);

    boolean estaEnRiesgoRepitencia(Long studentId);

    Attendance actualizarAsistencia(Long id, Attendance attendance);

    void eliminarAsistencia(Long id);
}
