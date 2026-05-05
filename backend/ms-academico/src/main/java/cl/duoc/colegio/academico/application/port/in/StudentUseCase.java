package cl.duoc.colegio.academico.application.port.in;

import cl.duoc.colegio.academico.domain.model.Student;
import java.util.List;

/**
 * Puerto de entrada — Casos de uso de Estudiantes.
 * Define el contrato que el adaptador REST (controlador) debe usar.
 * Nadie llama directamente al servicio — siempre a través de este puerto.
 */
public interface StudentUseCase {

    Student crearEstudiante(Student student);

    Student obtenerEstudiantePorId(Long id);

    Student obtenerEstudiantePorRut(String rut);

    List<Student> listarEstudiantes();

    List<Student> listarEstudiantesPorCurso(Integer curso);

    Student actualizarEstudiante(Long id, Student student);

    void eliminarEstudiante(Long id);
}
