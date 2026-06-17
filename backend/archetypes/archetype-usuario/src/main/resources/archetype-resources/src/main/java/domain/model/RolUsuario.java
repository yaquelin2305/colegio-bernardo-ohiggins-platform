#set( $symbol_dollar = '$' )
package ${package}.domain.model;

import lombok.Getter;

import java.util.List;

@Getter
public enum RolUsuario {

    ADMIN(List.of("notas", "asistencias", "estudiantes", "docentes", "apoderados", "cursos", "reportes-academicos", "usuarios", "configuracion")),
    DOCENTE(List.of("notas", "asistencias", "estudiantes", "cursos")),
    APODERADO(List.of("notas", "asistencias", "reportes-academicos")),
    ESTUDIANTE(List.of("notas", "asistencias"));

    private final List<String> recursos;

    RolUsuario(List<String> recursos) {
        this.recursos = recursos;
    }
}
