package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository;

import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio Spring Data JPA para StudentEntity.
 * Green IT: solo las queries necesarias — sin métodos genéricos innecesarios.
 */
public interface StudentJpaRepository extends JpaRepository<StudentEntity, Long> {

    Optional<StudentEntity> findByRut(String rut);

    List<StudentEntity> findByCurso(Integer curso);

    boolean existsByRut(String rut);
}
