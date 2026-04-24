package cl.duoc.colegio.academico.application.port.out;

import cl.duoc.colegio.academico.domain.model.Student;
import java.util.List;
import java.util.Optional;

/**
 * Puerto de salida — Repositorio de Estudiantes.
 * La aplicación define el contrato; la infraestructura lo implementa.
 * Desacopla totalmente el dominio de JPA/PostgreSQL.
 */
public interface StudentRepositoryPort {

    Student guardar(Student student);

    Optional<Student> buscarPorId(Long id);

    Optional<Student> buscarPorRut(String rut);

    List<Student> buscarTodos();

    List<Student> buscarPorCurso(Integer curso);

    boolean existePorRut(String rut);

    void eliminar(Long id);
}
