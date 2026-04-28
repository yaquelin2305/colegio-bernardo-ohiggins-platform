package cl.duoc.colegio.usuario.application.strategy;

import cl.duoc.colegio.usuario.domain.model.Permisos;
import cl.duoc.colegio.usuario.domain.model.Usuario;

/**
 * Interfaz del Patrón Strategy para Autorización.
 *
 * Cada rol implementa esta interfaz definiendo exactamente qué puede hacer.
 * El Gateway y los MS internos reciben el resultado en los claims del JWT.
 *
 * ¿Por qué Strategy aquí?
 * Porque el algoritmo de "qué puede ver/hacer este usuario" varía POR ROL,
 * y queremos sustituir ese algoritmo en tiempo de ejecución sin if/else en el
 * código cliente. Open/Closed Principle: abierto para extensión (nuevo rol),
 * cerrado para modificación (el código que llama no cambia).
 */
public interface AuthorizationStrategy {

    /**
     * Determina los permisos del usuario basándose en su rol y contexto.
     *
     * @param usuario el usuario autenticado
     * @return permisos construidos para este usuario
     */
    Permisos resolverPermisos(Usuario usuario);

    /**
     * Genera los claims adicionales para incluir en el JWT.
     * Cada rol puede incluir datos específicos en el token.
     *
     * @param usuario el usuario autenticado
     * @return mapa de claims adicionales
     */
    java.util.Map<String, Object> generarClaimsAdicionales(Usuario usuario);
}
