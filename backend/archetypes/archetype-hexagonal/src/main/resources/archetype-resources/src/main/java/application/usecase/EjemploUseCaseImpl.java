#set( $symbol_dollar = '$' )
package ${package}.application.usecase;

import ${package}.domain.model.Ejemplo;
import ${package}.domain.port.in.EjemploUseCase;
import ${package}.domain.port.out.EjemploRepositoryPort;
import ${package}.domain.exception.EjemploNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EjemploUseCaseImpl implements EjemploUseCase {

    private final EjemploRepositoryPort repositoryPort;

    @Override
    @Transactional(readOnly = true)
    public Optional<Ejemplo> obtenerPorId(Long id) {
        Optional<Ejemplo> result = repositoryPort.findById(id);
        if (result.isEmpty()) {
            throw new EjemploNotFoundException(id);
        }
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ejemplo> listarTodos() {
        return repositoryPort.findAll();
    }

    @Override
    public Ejemplo crear(Ejemplo ejemplo) {
        if (!ejemplo.esValido()) {
            throw new IllegalArgumentException("Nombre inválido (mín. 3 caracteres)");
        }
        return repositoryPort.save(ejemplo);
    }
}
