#set( $symbol_dollar = '$' )
package ${package}.application.usecase;

import ${package}.domain.model.Ejemplo;
import ${package}.domain.port.in.EjemploUseCase;
import ${package}.domain.port.out.EjemploRepositoryPort;
import ${package}.domain.exception.EjemploNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EjemploUseCaseImplTest {

    @Mock
    private EjemploRepositoryPort repositoryPort;

    private EjemploUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new EjemploUseCaseImpl(repositoryPort);
    }

    @Test
    void obtenerPorId_debeRetornarEjemplo() {
        Ejemplo esperado = Ejemplo.builder().id(1L).nombre("Test").descripcion("Desc").build();
        when(repositoryPort.findById(1L)).thenReturn(Optional.of(esperado));

        Optional<Ejemplo> result = useCase.obtenerPorId(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getNombre()).isEqualTo("Test");
    }

    @Test
    void obtenerPorId_debeLanzarExcepcionSiNoExiste() {
        when(repositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.obtenerPorId(99L))
                .isInstanceOf(EjemploNotFoundException.class);
    }
}
