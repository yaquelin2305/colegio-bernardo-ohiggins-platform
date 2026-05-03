package cl.duoc.colegio.academico.infrastructure.adapter.in.rest.dto;

import java.util.List;
import java.util.UUID;

/**
 * DTO de respuesta que cumple el contrato PUT /calificaciones/guardar.
 *
 * Transforma el modelo flexible de dominio (N notas por tipo)
 * en la estructura fija que define el ERD: nota_1, nota_2, nota_3, promedio.
 *
 * Regla de mapeo:
 *   nota_1 = primera nota registrada (índice 0)
 *   nota_2 = segunda nota registrada (índice 1)
 *   nota_3 = tercera nota registrada (índice 2)
 *   promedio = (nota_1 + nota_2 + nota_3) / notas_con_valor
 *
 * Si hay menos de 3 notas, los campos faltantes son null.
 * El promedio usa solo las notas presentes.
 */
public record CalificacionesContractDto(
        UUID usuarioUuid,
        Long asignaturaId,
        Double nota1,
        Double nota2,
        Double nota3,
        Double promedio
) {

    /**
     * Factory: construye el DTO de contrato a partir de una lista de notas de dominio.
     *
     * @param usuarioUuid  UUID del estudiante
     * @param asignaturaId ID de la asignatura
     * @param notas        Lista de valores de nota (1.0–7.0), cualquier tamaño
     * @return DTO con estructura fija nota_1/2/3 + promedio calculado
     */
    public static CalificacionesContractDto from(UUID usuarioUuid, Long asignaturaId,
                                                  List<Double> notas) {
        Double n1 = notas.size() > 0 ? notas.get(0) : null;
        Double n2 = notas.size() > 1 ? notas.get(1) : null;
        Double n3 = notas.size() > 2 ? notas.get(2) : null;

        double promedio = notas.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);

        // Redondear a 1 decimal (convención nota chilena)
        double promedioRedondeado = Math.round(promedio * 10.0) / 10.0;

        return new CalificacionesContractDto(usuarioUuid, asignaturaId,
                n1, n2, n3, promedioRedondeado);
    }
}
