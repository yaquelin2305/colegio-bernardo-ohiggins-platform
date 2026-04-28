package cl.duoc.colegio.usuario.domain.model;

import java.util.List;

/**
 * Value Object que representa los permisos que posee un usuario según su rol.
 *
 * Immutable — se construye una vez y no cambia.
 * Encapsula la lógica de qué recursos puede acceder un usuario.
 */
public final class Permisos {

    private final List<String> recursosPermitidos;
    private final List<String> operacionesPermitidas;
    private final boolean soloLectura;

    public Permisos(List<String> recursosPermitidos,
                    List<String> operacionesPermitidas,
                    boolean soloLectura) {
        this.recursosPermitidos = List.copyOf(recursosPermitidos);
        this.operacionesPermitidas = List.copyOf(operacionesPermitidas);
        this.soloLectura = soloLectura;
    }

    public boolean puedeAcceder(String recurso) {
        return recursosPermitidos.contains(recurso);
    }

    public boolean puedeEjecutar(String operacion) {
        return operacionesPermitidas.contains(operacion);
    }

    public List<String> getRecursosPermitidos() { return recursosPermitidos; }
    public List<String> getOperacionesPermitidas() { return operacionesPermitidas; }
    public boolean isSoloLectura() { return soloLectura; }
}
