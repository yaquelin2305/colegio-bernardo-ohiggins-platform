package cl.duoc.colegio.academico.application.service;

import cl.duoc.colegio.academico.application.port.in.ReportUseCase;
import cl.duoc.colegio.academico.application.port.out.AttendanceRepositoryPort;
import cl.duoc.colegio.academico.application.port.out.GradeRepositoryPort;
import cl.duoc.colegio.academico.application.port.out.StudentRepositoryPort;
import cl.duoc.colegio.academico.domain.exception.StudentNotFoundException;
import cl.duoc.colegio.academico.domain.model.AcademicReport;
import cl.duoc.colegio.academico.domain.model.Attendance;
import cl.duoc.colegio.academico.domain.model.Grade;
import cl.duoc.colegio.academico.domain.model.Student;
import cl.duoc.colegio.academico.infrastructure.factory.AcademicReportFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de reportes académicos.
 * Orquesta los repositorios y delega la creación del reporte al Factory Method.
 */
@Service
@Transactional(readOnly = true)
public class ReportService implements ReportUseCase {

    private final StudentRepositoryPort studentRepository;
    private final GradeRepositoryPort gradeRepository;
    private final AttendanceRepositoryPort attendanceRepository;

    public ReportService(StudentRepositoryPort studentRepository,
                         GradeRepositoryPort gradeRepository,
                         AttendanceRepositoryPort attendanceRepository) {
        this.studentRepository = studentRepository;
        this.gradeRepository = gradeRepository;
        this.attendanceRepository = attendanceRepository;
    }

    @Override
    public AcademicReport generarReporteEstudiante(Long studentId) {
        Student student = studentRepository.buscarPorId(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));

        List<Grade> grades = gradeRepository.buscarPorStudentId(studentId);
        List<Attendance> attendances = attendanceRepository.buscarPorStudentId(studentId);

        double porcentajeAsistencia = calcularPorcentaje(attendances);

        // Delegamos la creación al Factory Method — no mezclamos lógica de construcción aquí
        return AcademicReportFactory.crear(student, grades, porcentajeAsistencia);
    }

    private double calcularPorcentaje(List<Attendance> attendances) {
        if (attendances.isEmpty()) return 100.0;
        long presentes = attendances.stream().filter(Attendance::isPresente).count();
        return (presentes * 100.0) / attendances.size();
    }
}
