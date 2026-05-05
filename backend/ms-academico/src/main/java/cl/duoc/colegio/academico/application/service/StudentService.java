package cl.duoc.colegio.academico.application.service;

import cl.duoc.colegio.academico.application.port.in.StudentUseCase;
import cl.duoc.colegio.academico.application.port.out.StudentRepositoryPort;
import cl.duoc.colegio.academico.domain.exception.StudentNotFoundException;
import cl.duoc.colegio.academico.domain.model.Student;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio de estudiantes — Singleton gestionado por Spring.
 * Implementa el caso de uso StudentUseCase.
 * No conoce JPA ni REST — solo habla con puertos.
 */
@Service
@Transactional
public class StudentService implements StudentUseCase {

    private final StudentRepositoryPort studentRepository;

    // Inyección por constructor — buena práctica (testeable, inmutable)
    public StudentService(StudentRepositoryPort studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Override
    public Student crearEstudiante(Student student) {
        return studentRepository.guardar(student);
    }

    @Override
    @Transactional(readOnly = true)
    public Student obtenerEstudiantePorId(Long id) {
        return studentRepository.buscarPorId(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Student obtenerEstudiantePorRut(String rut) {
        return studentRepository.buscarPorRut(rut)
                .orElseThrow(() -> new StudentNotFoundException(rut));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> listarEstudiantes() {
        return studentRepository.buscarTodos();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Student> listarEstudiantesPorCurso(Integer curso) {
        return studentRepository.buscarPorCurso(curso);
    }

    @Override
    public Student actualizarEstudiante(Long id, Student student) {
        // Verificar que existe antes de actualizar
        studentRepository.buscarPorId(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        return studentRepository.guardar(student);
    }

    @Override
    public void eliminarEstudiante(Long id) {
        studentRepository.buscarPorId(id)
                .orElseThrow(() -> new StudentNotFoundException(id));
        studentRepository.eliminar(id);
    }
}
