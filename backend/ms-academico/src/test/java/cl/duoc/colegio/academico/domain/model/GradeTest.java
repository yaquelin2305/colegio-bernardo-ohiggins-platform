package cl.duoc.colegio.academico.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests unitarios para la entidad Grade.
 */
@DisplayName("Grade — Validación de notas")
class GradeTest {

    private static final UUID TEST_UUID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final Long ASIGNATURA_ID = 100L;

    @Test
    @DisplayName("Nota válida 1.0 se crea correctamente")
    void notaMinima_valida() {
        Grade grade = new Grade(1L, TEST_UUID, ASIGNATURA_ID, 1.0, "PRUEBA", null);
        assertThat(grade.getNota()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("Nota válida 7.0 se crea correctamente")
    void notaMaxima_valida() {
        Grade grade = new Grade(1L, TEST_UUID, ASIGNATURA_ID, 7.0, "PRUEBA", null);
        assertThat(grade.getNota()).isEqualTo(7.0);
    }

    @Test
    @DisplayName("Nota mayor a 7.0 lanza IllegalArgumentException")
    void notaMayor7_lanzaExcepcion() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Grade(1L, TEST_UUID, ASIGNATURA_ID, 7.1, "PRUEBA", null))
            .withMessageContaining("7.1");
    }

    @Test
    @DisplayName("Nota menor a 1.0 lanza IllegalArgumentException")
    void notaMenor1_lanzaExcepcion() {
        assertThatIllegalArgumentException()
            .isThrownBy(() -> new Grade(1L, TEST_UUID, ASIGNATURA_ID, 0.9, "PRUEBA", null))
            .withMessageContaining("0.9");
    }

    @Test
    @DisplayName("Nota 4.0 es aprobatoria")
    void nota4_esAprobatoria() {
        Grade grade = new Grade(1L, TEST_UUID, ASIGNATURA_ID, 4.0, "PRUEBA", null);
        assertThat(grade.esAprobatoria()).isTrue();
    }

    @Test
    @DisplayName("Nota 3.9 NO es aprobatoria")
    void nota3punto9_noEsAprobatoria() {
        Grade grade = new Grade(1L, TEST_UUID, ASIGNATURA_ID, 3.9, "PRUEBA", null);
        assertThat(grade.esAprobatoria()).isFalse();
    }
}
