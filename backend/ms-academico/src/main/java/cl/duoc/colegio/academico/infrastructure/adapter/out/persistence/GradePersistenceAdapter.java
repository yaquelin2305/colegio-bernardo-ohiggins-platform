package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.academico.application.port.out.GradeRepositoryPort;
import cl.duoc.colegio.academico.domain.model.Grade;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.mapper.GradePersistenceMapper;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository.GradeJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia para Notas.
 */
@Component
public class GradePersistenceAdapter implements GradeRepositoryPort {

    private final GradeJpaRepository jpaRepository;
    private final GradePersistenceMapper mapper;

    public GradePersistenceAdapter(GradeJpaRepository jpaRepository,
                                   GradePersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Grade guardar(Grade grade) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(grade)));
    }

    @Override
    public Optional<Grade> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Grade> buscarPorStudentId(Long studentId) {
        return jpaRepository.findByStudentId(studentId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Grade> buscarPorStudentIdYAsignatura(Long studentId, String asignatura) {
        return jpaRepository.findByStudentIdAndAsignatura(studentId, asignatura).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
