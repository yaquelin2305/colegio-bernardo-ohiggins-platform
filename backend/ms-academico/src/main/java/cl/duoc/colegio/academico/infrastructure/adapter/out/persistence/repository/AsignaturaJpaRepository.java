package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository;

import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.AsignaturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AsignaturaJpaRepository extends JpaRepository<AsignaturaEntity, Long> {}
