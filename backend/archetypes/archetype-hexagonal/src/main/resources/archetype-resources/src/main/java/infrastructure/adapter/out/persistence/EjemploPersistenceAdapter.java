#set( $symbol_dollar = '$' )
package ${package}.infrastructure.adapter.out.persistence;

import ${package}.domain.model.Ejemplo;
import ${package}.domain.port.out.EjemploRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EjemploPersistenceAdapter implements EjemploRepositoryPort {

    private final EjemploJpaRepository jpaRepository;

    @Override
    public Optional<Ejemplo> findById(Long id) {
        return jpaRepository.findById(id).map(EjemploEntity::toDomain);
    }

    @Override
    public List<Ejemplo> findAll() {
        return jpaRepository.findAll().stream()
                .map(EjemploEntity::toDomain)
                .toList();
    }

    @Override
    public Ejemplo save(Ejemplo ejemplo) {
        EjemploEntity entity = EjemploEntity.fromDomain(ejemplo);
        EjemploEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
}
