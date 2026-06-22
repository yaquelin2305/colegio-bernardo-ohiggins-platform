package cl.duoc.colegio.academico.application.service;

import cl.duoc.colegio.academico.application.port.out.StudentRepositoryPort;
import cl.duoc.colegio.academico.domain.exception.StudentNotFoundException;
import cl.duoc.colegio.academico.domain.model.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService — Pruebas Unitarias")
class StudentServiceTest {

    @Mock
    private StudentRepositoryPort studentRepository;

    @InjectMocks
    private StudentService studentService;

    @Test
    @DisplayName("crearEstudiante guarda y retorna estudiante")
    void crearEstudiante_guardaYRetorna() {
        Student s = new Student(null, "12345678-9", "Juan", "Pérez", 3);
        when(studentRepository.guardar(any(Student.class))).thenReturn(s);

        Student result = studentService.crearEstudiante(s);

        assertThat(result.getRut()).isEqualTo("12345678-9");
        verify(studentRepository).guardar(s);
    }

    @Test
    @DisplayName("obtenerEstudiantePorId encuentra estudiante")
    void obtenerEstudiantePorId_existe_retornaEstudiante() {
        Student s = new Student(null, "12345678-9", "Juan", "Pérez", 3);
        when(studentRepository.buscarPorId(1L)).thenReturn(Optional.of(s));

        Student result = studentService.obtenerEstudiantePorId(1L);

        assertThat(result).isNotNull();
        assertThat(result.getNombre()).isEqualTo("Juan");
    }

    @Test
    @DisplayName("obtenerEstudiantePorId no encuentra lanza StudentNotFoundException")
    void obtenerEstudiantePorId_noExiste_lanzaExcepcion() {
        when(studentRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.obtenerEstudiantePorId(99L))
                .isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    @DisplayName("obtenerEstudiantePorRut encuentra estudiante")
    void obtenerEstudiantePorRut_existe_retornaEstudiante() {
        Student s = new Student(null, "12345678-9", "Juan", "Pérez", 3);
        when(studentRepository.buscarPorRut("12345678-9")).thenReturn(Optional.of(s));

        Student result = studentService.obtenerEstudiantePorRut("12345678-9");

        assertThat(result).isNotNull();
        assertThat(result.getRut()).isEqualTo("12345678-9");
    }

    @Test
    @DisplayName("obtenerEstudiantePorRut no encuentra lanza StudentNotFoundException")
    void obtenerEstudiantePorRut_noExiste_lanzaExcepcion() {
        when(studentRepository.buscarPorRut("99999999-9")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.obtenerEstudiantePorRut("99999999-9"))
                .isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    @DisplayName("listarEstudiantes retorna lista")
    void listarEstudiantes_retornaLista() {
        List<Student> lista = List.of(new Student(null, "11111111-1", "A", "B", 1));
        when(studentRepository.buscarTodos()).thenReturn(lista);

        List<Student> result = studentService.listarEstudiantes();

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("listarEstudiantesPorCurso retorna lista")
    void listarEstudiantesPorCurso_retornaLista() {
        List<Student> lista = List.of(new Student(null, "11111111-1", "A", "B", 3));
        when(studentRepository.buscarPorCurso(3)).thenReturn(lista);

        List<Student> result = studentService.listarEstudiantesPorCurso(3);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("actualizarEstudiante modifica datos")
    void actualizarEstudiante_existe_modifica() {
        Student nuevo = new Student(null, "12345678-9", "Nuevo", "Nombre", 5);
        when(studentRepository.buscarPorId(1L)).thenReturn(Optional.of(new Student(null, "x", "x", "x", 1)));
        when(studentRepository.guardar(any(Student.class))).thenReturn(nuevo);

        Student result = studentService.actualizarEstudiante(1L, nuevo);

        assertThat(result.getNombre()).isEqualTo("Nuevo");
        verify(studentRepository).guardar(nuevo);
    }

    @Test
    @DisplayName("actualizarEstudiante no existe lanza StudentNotFoundException")
    void actualizarEstudiante_noExiste_lanzaExcepcion() {
        when(studentRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.actualizarEstudiante(99L, new Student(null, "x", "x", "x", 1)))
                .isInstanceOf(StudentNotFoundException.class);
    }

    @Test
    @DisplayName("eliminarEstudiante elimina si existe")
    void eliminarEstudiante_existe_elimina() {
        when(studentRepository.buscarPorId(1L)).thenReturn(Optional.of(new Student(null, "x", "x", "x", 1)));

        studentService.eliminarEstudiante(1L);

        verify(studentRepository).eliminar(1L);
    }

    @Test
    @DisplayName("eliminarEstudiante no existe lanza StudentNotFoundException")
    void eliminarEstudiante_noExiste_lanzaExcepcion() {
        when(studentRepository.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> studentService.eliminarEstudiante(99L))
                .isInstanceOf(StudentNotFoundException.class);
    }
}
