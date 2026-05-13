#set( $symbol_dollar = '$' )
package ${package}.infrastructure.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EjemploJpaRepository extends JpaRepository<EjemploEntity, Long> {
}
