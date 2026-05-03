package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.mapper;

import cl.duoc.colegio.academico.domain.model.Grade;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.GradeEntity;
import org.springframework.stereotype.Component;

/**
 * Mapper: Grade (dominio flexible) <-> GradeEntity (JPA — estructura ERD).
 *
 * El dominio maneja una nota por registro. La entidad persiste la estructura
 * fija del ERD (nota_1, nota_2, nota_3, promedio).
 * La conversión N→3 la hace GradeContractMapper en la capa de aplicación.
 *
 * Aquí mapeamos 1:1 para el flujo de lectura/escritura individual.
 * nota_1 = nota del dominio (campo principal).
 */
@Component
public class GradePersistenceMapper {

    public Grade toDomain(GradeEntity entity) {
        // Tomamos nota_1 como la nota representativa de este registro
        double notaValor = entity.getNota1() != null ? entity.getNota1() : 0.0;
        return new Grade(
                entity.getId(),
                entity.getUsuarioUuid(),
                entity.getAsignaturaId(),
                notaValor,
                "REGISTRADA",  // tipo sintético para registros del contrato
                null
        );
    }

    public GradeEntity toEntity(Grade domain) {
        return GradeEntity.builder()
                .id(domain.getId())
                .usuarioUuid(domain.getUsuarioUuid())
                .asignaturaId(domain.getAsignaturaId())
                .nota1(domain.getNota())
                .nota2(null)
                .nota3(null)
                .promedio(domain.getNota())
                .build();
    }
}
