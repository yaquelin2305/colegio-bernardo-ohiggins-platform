package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.in.StudentUseCase;
import cl.duoc.colegio.academico.domain.model.Student;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.StudentRequest;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.StudentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para Estudiantes.
 * Adaptador de entrada — traduce HTTP a llamadas al caso de uso.
 * NO contiene lógica de negocio — solo traducción y delegación.
 */
@RestController
@RequestMapping("/api/v1/estudiantes")
@Tag(name = "Estudiantes", description = "Gestión de estudiantes del colegio")
public class StudentController {

    private final StudentUseCase studentUseCase;

    public StudentController(StudentUseCase studentUseCase) {
        this.studentUseCase = studentUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear estudiante", description = "Registra un nuevo estudiante en el sistema")
    public ResponseEntity<StudentResponse> crear(@Valid @RequestBody StudentRequest request) {
        Student student = toStudent(null, request);
        Student created = studentUseCase.crearEstudiante(student);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener estudiante por ID")
    public ResponseEntity<StudentResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(toResponse(studentUseCase.obtenerEstudiantePorId(id)));
    }

    @GetMapping("/rut/{rut}")
    @Operation(summary = "Obtener estudiante por RUT")
    public ResponseEntity<StudentResponse> obtenerPorRut(@PathVariable String rut) {
        return ResponseEntity.ok(toResponse(studentUseCase.obtenerEstudiantePorRut(rut)));
    }

    @GetMapping
    @Operation(summary = "Listar todos los estudiantes")
    public ResponseEntity<List<StudentResponse>> listar() {
        List<StudentResponse> response = studentUseCase.listarEstudiantes().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/curso/{curso}")
    @Operation(summary = "Listar estudiantes por curso")
    public ResponseEntity<List<StudentResponse>> listarPorCurso(@PathVariable Integer curso) {
        List<StudentResponse> response = studentUseCase.listarEstudiantesPorCurso(curso).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar estudiante")
    public ResponseEntity<StudentResponse> actualizar(@PathVariable Long id,
                                                       @Valid @RequestBody StudentRequest request) {
        Student updated = studentUseCase.actualizarEstudiante(id, toStudent(id, request));
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Eliminar estudiante")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        studentUseCase.eliminarEstudiante(id);
        return ResponseEntity.noContent().build();
    }

    // ===== MAPPERS REST (solo se usan en este controlador) =====
    private Student toStudent(Long id, StudentRequest req) {
        return new Student(id, req.getRut(), req.getNombre(), req.getApellido(), req.getCurso());
    }

    private StudentResponse toResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .rut(student.getRut())
                .nombre(student.getNombre())
                .apellido(student.getApellido())
                .curso(student.getCurso())
                .nombreCompleto(student.getNombreCompleto())
                .build();
    }
}
