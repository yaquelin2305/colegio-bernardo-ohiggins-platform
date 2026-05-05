package cl.duoc.colegio.academico.application.port.out;

import cl.duoc.colegio.academico.domain.model.Attendance;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida — Repositorio de Asistencia.
 */
public interface AttendanceRepositoryPort {

    Attendance guardar(Attendance attendance);

    Optional<Attendance> buscarPorId(Long id);

    List<Attendance> buscarPorStudentId(Long studentId);

    List<Attendance> buscarPorStudentIdYFecha(Long studentId, LocalDate fecha);

    void eliminar(Long id);
}
