package cl.duoc.colegio.academico.infrastructure.adapter.in.rest;

import cl.duoc.colegio.academico.application.port.in.AttendanceUseCase;
import cl.duoc.colegio.academico.domain.model.Attendance;
import cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto.AttendanceRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para Asistencia.
 */
@RestController
@RequestMapping("/api/v1/asistencias")
@Tag(name = "Asistencias", description = "Registro y consulta de asistencia escolar")
public class AttendanceController {

    private final AttendanceUseCase attendanceUseCase;

    public AttendanceController(AttendanceUseCase attendanceUseCase) {
        this.attendanceUseCase = attendanceUseCase;
    }

    @PostMapping
    @Operation(summary = "Registrar asistencia")
    public ResponseEntity<Attendance> registrar(@Valid @RequestBody AttendanceRequest request) {
        Attendance attendance = new Attendance(null, request.getStudentId(),
                request.getAsignatura(), request.getFecha(),
                request.getPresente(), request.getJustificacion());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(attendanceUseCase.registrarAsistencia(attendance));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener registro de asistencia por ID")
    public ResponseEntity<Attendance> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceUseCase.obtenerAsistenciaPorId(id));
    }

    @GetMapping("/estudiante/{studentId}")
    @Operation(summary = "Listar asistencias de un estudiante")
    public ResponseEntity<List<Attendance>> listarPorEstudiante(@PathVariable Long studentId) {
        return ResponseEntity.ok(attendanceUseCase.listarAsistenciasPorEstudiante(studentId));
    }

    @GetMapping("/estudiante/{studentId}/fecha/{fecha}")
    @Operation(summary = "Listar asistencias de un estudiante por fecha")
    public ResponseEntity<List<Attendance>> listarPorFecha(
            @PathVariable Long studentId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(
                attendanceUseCase.listarAsistenciasPorEstudianteYFecha(studentId, fecha));
    }

    @GetMapping("/estudiante/{studentId}/porcentaje")
    @Operation(summary = "Calcular porcentaje de asistencia de un estudiante")
    public ResponseEntity<Map<String, Object>> calcularPorcentaje(@PathVariable Long studentId) {
        double porcentaje = attendanceUseCase.calcularPorcentajeAsistencia(studentId);
        boolean enRiesgo = attendanceUseCase.estaEnRiesgoRepitencia(studentId);
        return ResponseEntity.ok(Map.of(
                "studentId", studentId,
                "porcentajeAsistencia", porcentaje,
                "enRiesgoRepitencia", enRiesgo
        ));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar registro de asistencia")
    public ResponseEntity<Attendance> actualizar(@PathVariable Long id,
                                                  @Valid @RequestBody AttendanceRequest request) {
        Attendance attendance = new Attendance(id, request.getStudentId(),
                request.getAsignatura(), request.getFecha(),
                request.getPresente(), request.getJustificacion());
        return ResponseEntity.ok(attendanceUseCase.actualizarAsistencia(id, attendance));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar registro de asistencia")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        attendanceUseCase.eliminarAsistencia(id);
        return ResponseEntity.noContent().build();
    }
}
