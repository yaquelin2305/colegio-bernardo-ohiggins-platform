#set( $symbol_dollar = '$' )
package ${package}.domain.exception;

public class UsuarioInactivoException extends RuntimeException {

    public UsuarioInactivoException() {
        super("El usuario se encuentra desactivado");
    }
}
