package cl.duoc.colegio.academico.application.service;

import cl.duoc.colegio.academico.application.port.in.AttendanceUseCase;
import cl.duoc.colegio.academico.application.port.out.AttendanceRepositoryPort;
import cl.duoc.colegio.academico.application.port.out.StudentRepositoryPort;
import cl.duoc.colegio.academico.domain.exception.AttendanceNotFoundException;
import cl.duoc.colegio.academico.domain.exception.StudentNotFoundException;
import cl.duoc.colegio.academico.domain.model.Attendance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio de asistencia.
 * Encapsula la lógica de cálculo y detección de riesgo de repitencia.
 */
@Service
@Transactional
public class AttendanceService implements AttendanceUseCase {

    // Constante de negocio: umbral mínimo de asistencia requerido (Chile: 85%)
    private static final double UMBRAL_ASISTENCIA_MINIMO = 85.0;

    private final AttendanceRepositoryPort attendanceRepository;
    private final StudentRepositoryPort studentRepository;

    public AttendanceService(AttendanceRepositoryPort attendanceRepository,
                             StudentRepositoryPort studentRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public Attendance registrarAsistencia(Attendance attendance) {
        studentRepository.buscarPorId(attendance.getStudentId())
                .orElseThrow(() -> new StudentNotFoundException(attendance.getStudentId()));
        return attendanceRepository.guardar(attendance);
    }

    @Override
    @Transactional(readOnly = true)
    public Attendance obtenerAsistenciaPorId(Long id) {
        return attendanceRepository.buscarPorId(id)
                .orElseThrow(() -> new AttendanceNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> listarAsistenciasPorEstudiante(Long studentId) {
        studentRepository.buscarPorId(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        return attendanceRepository.buscarPorStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Attendance> listarAsistenciasPorEstudianteYFecha(Long studentId, LocalDate fecha) {
        return attendanceRepository.buscarPorStudentIdYFecha(studentId, fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public double calcularPorcentajeAsistencia(Long studentId) {
        List<Attendance> registros = listarAsistenciasPorEstudiante(studentId);
        if (registros.isEmpty()) return 100.0;
        long presentes = registros.stream().filter(Attendance::isPresente).count();
        return (presentes * 100.0) / registros.size();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaEnRiesgoRepitencia(Long studentId) {
        return calcularPorcentajeAsistencia(studentId) < UMBRAL_ASISTENCIA_MINIMO;
    }

    @Override
    public Attendance actualizarAsistencia(Long id, Attendance attendance) {
        attendanceRepository.buscarPorId(id)
                .orElseThrow(() -> new AttendanceNotFoundException(id));
        return attendanceRepository.guardar(attendance);
    }

    @Override
    public void eliminarAsistencia(Long id) {
        attendanceRepository.buscarPorId(id)
                .orElseThrow(() -> new AttendanceNotFoundException(id));
        attendanceRepository.eliminar(id);
    }
}
