import { Pencil, Trash2, X } from 'lucide-react';

function TablaUsuarios({ lista, tabActiva, columnas, usuarioEditando, confirmarEliminarId, onEditar, onEliminar, onToggleConfirmar }) {
  return (
    <section className="gestion-usuarios__seccion" aria-label={`Listado de ${tabActiva}`}>
      <h2 className="gestion-usuarios__titulo-seccion">
        {tabActiva.charAt(0).toUpperCase() + tabActiva.slice(1)} registrados
        <span className="gestion-usuarios__conteo">{lista.length}</span>
      </h2>

      <div className="gestion-usuarios__tabla-wrapper">
        <table className="gestion-usuarios__tabla">
          <thead>
            <tr>
              {columnas.map(col => <th key={col} scope="col">{col}</th>)}
              <th scope="col">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {lista.length === 0 && (
              <tr>
                <td colSpan={columnas.length + 1} className="gestion-usuarios__tabla-vacia">
                  No hay {tabActiva} registrados.
                </td>
              </tr>
            )}
            {lista.map(usuario => (
              <>
                <tr
                  key={usuario.id}
                  className={usuarioEditando?.id === usuario.id ? 'gestion-usuarios__fila--editando' : ''}
                >
                  <td className="gestion-usuarios__celda-rut">{usuario.rut}</td>
                  <td>{usuario.nombres} {usuario.apellidos}</td>
                  <td className="gestion-usuarios__celda-email">{usuario.email}</td>
                  {tabActiva === 'estudiantes' && (
                    <td className="gestion-usuarios__celda-apoderado">{usuario.apoderado}</td>
                  )}
                  <td>
                    <div className="gestion-usuarios__acciones-fila">
                      <button
                        className="gestion-usuarios__btn-editar"
                        onClick={() => onEditar(usuario)}
                        aria-label={`Editar ${usuario.nombres}`}
                      >
                        <Pencil size={14} />
                      </button>
                      <button
                        className="gestion-usuarios__btn-eliminar"
                        onClick={() => onToggleConfirmar(usuario.id)}
                        aria-label={`Eliminar ${usuario.nombres}`}
                      >
                        <Trash2 size={14} />
                      </button>
                    </div>
                  </td>
                </tr>

                {confirmarEliminarId === usuario.id && (
                  <tr key={`confirm-${usuario.id}`} className="gestion-usuarios__fila-confirmacion">
                    <td colSpan={columnas.length + 1}>
                      <div className="gestion-usuarios__confirmacion">
                        <p className="gestion-usuarios__confirmacion-texto">
                          ¿Eliminar a <strong>{usuario.nombres} {usuario.apellidos}</strong>? Esta acción no se puede deshacer.
                        </p>
                        <div className="gestion-usuarios__confirmacion-acciones">
                          <button
                            className="gestion-usuarios__btn-confirmar-eliminar"
                            onClick={() => onEliminar(usuario.id)}
                          >
                            <Trash2 size={14} aria-hidden="true" />
                            Sí, eliminar
                          </button>
                          <button
                            className="gestion-usuarios__btn-cancelar"
                            onClick={() => onToggleConfirmar(null)}
                          >
                            <X size={14} aria-hidden="true" />
                            Cancelar
                          </button>
                        </div>
                      </div>
                    </td>
                  </tr>
                )}
              </>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  );
}

export default TablaUsuarios;
