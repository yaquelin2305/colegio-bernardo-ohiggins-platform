package cl.duoc.colegio.academico.application.service;

import cl.duoc.colegio.academico.application.port.in.GradeUseCase;
import cl.duoc.colegio.academico.application.port.out.GradeRepositoryPort;
import cl.duoc.colegio.academico.application.port.out.StudentRepositoryPort;
import cl.duoc.colegio.academico.domain.exception.GradeNotFoundException;
import cl.duoc.colegio.academico.domain.exception.StudentNotFoundException;
import cl.duoc.colegio.academico.domain.model.Grade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de notas académicas.
 * Contiene la lógica central de cálculo de promedios.
 */
@Service
@Transactional
public class GradeService implements GradeUseCase {

    private final GradeRepositoryPort gradeRepository;
    private final StudentRepositoryPort studentRepository;

    public GradeService(GradeRepositoryPort gradeRepository,
                        StudentRepositoryPort studentRepository) {
        this.gradeRepository = gradeRepository;
        this.studentRepository = studentRepository;
    }

    @Override
    public Grade registrarNota(Grade grade) {
        // Validar que el estudiante existe antes de registrar nota
        studentRepository.buscarPorId(grade.getStudentId())
                .orElseThrow(() -> new StudentNotFoundException(grade.getStudentId()));
        return gradeRepository.guardar(grade);
    }

    @Override
    @Transactional(readOnly = true)
    public Grade obtenerNotaPorId(Long id) {
        return gradeRepository.buscarPorId(id)
                .orElseThrow(() -> new GradeNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> listarNotasPorEstudiante(Long studentId) {
        studentRepository.buscarPorId(studentId)
                .orElseThrow(() -> new StudentNotFoundException(studentId));
        return gradeRepository.buscarPorStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Grade> listarNotasPorEstudianteYAsignatura(Long studentId, String asignatura) {
        return gradeRepository.buscarPorStudentIdYAsignatura(studentId, asignatura);
    }

    @Override
    @Transactional(readOnly = true)
    public double calcularPromedioEstudiante(Long studentId) {
        List<Grade> notas = listarNotasPorEstudiante(studentId);
        if (notas.isEmpty()) return 0.0;
        return notas.stream()
                .mapToDouble(Grade::getNota)
                .average()
                .orElse(0.0);
    }

    @Override
    @Transactional(readOnly = true)
    public double calcularPromedioEstudiantePorAsignatura(Long studentId, String asignatura) {
        List<Grade> notas = listarNotasPorEstudianteYAsignatura(studentId, asignatura);
        if (notas.isEmpty()) return 0.0;
        return notas.stream()
                .mapToDouble(Grade::getNota)
                .average()
                .orElse(0.0);
    }

    @Override
    public Grade actualizarNota(Long id, Grade grade) {
        gradeRepository.buscarPorId(id)
                .orElseThrow(() -> new GradeNotFoundException(id));
        return gradeRepository.guardar(grade);
    }

    @Override
    public void eliminarNota(Long id) {
        gradeRepository.buscarPorId(id)
                .orElseThrow(() -> new GradeNotFoundException(id));
        gradeRepository.eliminar(id);
    }
}
