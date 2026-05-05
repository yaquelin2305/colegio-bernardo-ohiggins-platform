package cl.duoc.colegio.academico.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para la entidad de dominio Student.
 * Cobertura: lógica de cálculo de promedios y detección de riesgo.
 */
@DisplayName("Student — Lógica de dominio")
class StudentTest {

    private Student student;

    @BeforeEach
    void setUp() {
        student = new Student(1L, "12345678-9", "Juan", "Pérez", 8);
    }

    @Nested
    @DisplayName("calcularPromedio()")
    class CalcularPromedio {

        @Test
        @DisplayName("Retorna 0.0 cuando no hay notas registradas")
        void sinNotas_retornaCero() {
            assertThat(student.calcularPromedio()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Calcula correctamente con una sola nota")
        void unaNota_retornaEsaNota() {
            student.agregarNota(grade(5.5));
            assertThat(student.calcularPromedio()).isEqualTo(5.5);
        }

        @Test
        @DisplayName("Calcula promedio correcto con múltiples notas")
        void variasNotas_promedioExacto() {
            student.agregarNota(grade(4.0));
            student.agregarNota(grade(6.0));
            student.agregarNota(grade(5.0));
            // (4.0 + 6.0 + 5.0) / 3 = 5.0
            assertThat(student.calcularPromedio()).isEqualTo(5.0);
        }

        @Test
        @DisplayName("Promedio con nota mínima 1.0 y máxima 7.0")
        void notaExtremas_promedioCorrector() {
            student.agregarNota(grade(1.0));
            student.agregarNota(grade(7.0));
            assertThat(student.calcularPromedio()).isEqualTo(4.0);
        }

        @Test
        @DisplayName("Promedio bajo 4.0 indica reprobado")
        void promedioReprobatorio_estaReprobadoEsTrue() {
            student.agregarNota(grade(2.0));
            student.agregarNota(grade(3.5));
            assertThat(student.calcularPromedio()).isLessThan(4.0);
            assertThat(student.estaReprobado()).isTrue();
        }

        @Test
        @DisplayName("Promedio exactamente 4.0 NO es reprobado")
        void promedio4punto0_noEstaReprobado() {
            student.agregarNota(grade(4.0));
            assertThat(student.estaReprobado()).isFalse();
        }
    }

    @Nested
    @DisplayName("calcularPorcentajeAsistencia()")
    class CalcularPorcentajeAsistencia {

        @Test
        @DisplayName("Retorna 100.0 cuando no hay registros de asistencia")
        void sinRegistros_retornaCien() {
            assertThat(student.calcularPorcentajeAsistencia()).isEqualTo(100.0);
        }

        @Test
        @DisplayName("100% cuando todos los registros son presentes")
        void todosPresentes_cientoPorciento() {
            student.agregarAsistencia(asistencia(true));
            student.agregarAsistencia(asistencia(true));
            student.agregarAsistencia(asistencia(true));
            assertThat(student.calcularPorcentajeAsistencia()).isEqualTo(100.0);
        }

        @Test
        @DisplayName("0% cuando todos los registros son ausentes")
        void todosAusentes_ceroPorciento() {
            student.agregarAsistencia(asistencia(false));
            student.agregarAsistencia(asistencia(false));
            assertThat(student.calcularPorcentajeAsistencia()).isEqualTo(0.0);
        }

        @Test
        @DisplayName("Calcula porcentaje correcto con presencias y ausencias mixtas")
        void mixto_porcentajeCorrecto() {
            // 3 presentes, 1 ausente = 75%
            student.agregarAsistencia(asistencia(true));
            student.agregarAsistencia(asistencia(true));
            student.agregarAsistencia(asistencia(true));
            student.agregarAsistencia(asistencia(false));
            assertThat(student.calcularPorcentajeAsistencia()).isEqualTo(75.0);
        }
    }

    @Nested
    @DisplayName("estaEnRiesgoRepitenciaPorAsistencia()")
    class RiesgoRepitencia {

        @Test
        @DisplayName("Sin registros no hay riesgo (100% asistencia)")
        void sinRegistros_noHayRiesgo() {
            assertThat(student.estaEnRiesgoRepitenciaPorAsistencia()).isFalse();
        }

        @Test
        @DisplayName("Con 84% de asistencia está en riesgo (< 85%)")
        void asistencia84_enRiesgo() {
            // 84 de 100 presentes
            for (int i = 0; i < 84; i++) student.agregarAsistencia(asistencia(true));
            for (int i = 0; i < 16; i++) student.agregarAsistencia(asistencia(false));
            assertThat(student.estaEnRiesgoRepitenciaPorAsistencia()).isTrue();
        }

        @Test
        @DisplayName("Con exactamente 85% de asistencia NO está en riesgo")
        void asistencia85_noEstaEnRiesgo() {
            for (int i = 0; i < 85; i++) student.agregarAsistencia(asistencia(true));
            for (int i = 0; i < 15; i++) student.agregarAsistencia(asistencia(false));
            assertThat(student.estaEnRiesgoRepitenciaPorAsistencia()).isFalse();
        }
    }

    @Nested
    @DisplayName("Validaciones de construcción")
    class Validaciones {

        @Test
        @DisplayName("RUT nulo lanza NullPointerException")
        void rutNulo_lanzaExcepcion() {
            assertThatNullPointerException()
                .isThrownBy(() -> new Student(1L, null, "Juan", "Pérez", 8));
        }

        @Test
        @DisplayName("Nombre nulo lanza NullPointerException")
        void nombreNulo_lanzaExcepcion() {
            assertThatNullPointerException()
                .isThrownBy(() -> new Student(1L, "12345678-9", null, "Pérez", 8));
        }

        @Test
        @DisplayName("getNombreCompleto retorna nombre y apellido concatenados")
        void getNombreCompleto_correcto() {
            assertThat(student.getNombreCompleto()).isEqualTo("Juan Pérez");
        }
    }

    // ===== HELPERS =====
    private Grade grade(double nota) {
        return new Grade(null, 1L, "Matemáticas", nota, "PRUEBA", LocalDate.now(), null);
    }

    private Attendance asistencia(boolean presente) {
        return new Attendance(null, 1L, "Matemáticas", LocalDate.now(), presente, null);
    }
}
