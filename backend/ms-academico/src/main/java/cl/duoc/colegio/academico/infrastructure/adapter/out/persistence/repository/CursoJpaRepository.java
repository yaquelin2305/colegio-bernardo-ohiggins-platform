package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository;

import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.CursoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoJpaRepository extends JpaRepository<CursoEntity, Long> {}
