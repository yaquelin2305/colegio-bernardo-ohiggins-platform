#set( $symbol_dollar = '$' )
package ${package}.domain.port.out;

public interface TokenPort {

    String generarToken(${package}.domain.model.Usuario usuario, java.util.List<String> recursos);

    boolean validarToken(String token);
}
