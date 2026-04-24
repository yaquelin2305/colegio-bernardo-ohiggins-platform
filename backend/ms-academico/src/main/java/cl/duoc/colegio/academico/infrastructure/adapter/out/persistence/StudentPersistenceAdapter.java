package cl.duoc.colegio.academico.infrastructure.adapter.out.persistence;

import cl.duoc.colegio.academico.application.port.out.StudentRepositoryPort;
import cl.duoc.colegio.academico.domain.model.Student;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.mapper.StudentPersistenceMapper;
import cl.duoc.colegio.academico.infrastructure.adapter.out.persistence.repository.StudentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adaptador de persistencia — implementa el puerto de salida StudentRepositoryPort.
 * PATRÓN Repository: encapsula toda la lógica de acceso a datos.
 * El dominio y la aplicación NUNCA importan JPA — solo este adaptador lo hace.
 */
@Component
public class StudentPersistenceAdapter implements StudentRepositoryPort {

    private final StudentJpaRepository jpaRepository;
    private final StudentPersistenceMapper mapper;

    public StudentPersistenceAdapter(StudentJpaRepository jpaRepository,
                                     StudentPersistenceMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Student guardar(Student student) {
        var entity = mapper.toEntity(student);
        var saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Student> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Student> buscarPorRut(String rut) {
        return jpaRepository.findByRut(rut).map(mapper::toDomain);
    }

    @Override
    public List<Student> buscarTodos() {
        return jpaRepository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> buscarPorCurso(Integer curso) {
        return jpaRepository.findByCurso(curso).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existePorRut(String rut) {
        return jpaRepository.existsByRut(rut);
    }

    @Override
    public void eliminar(Long id) {
        jpaRepository.deleteById(id);
    }
}
