import { ThumbsUp, ThumbsDown, Plus, X, ChevronDown, ChevronUp } from 'lucide-react';
import { useState } from 'react';

function TablaAnotaciones({ alumnos, anotacionesPorAlumno, panelActivo, formulario, onTogglePanel, onTipoChange, onDescripcionChange, onGuardar, onCancelar, guardadoDeshabilitado = false }) {
  const [historialAbierto, setHistorialAbierto] = useState(null);

  function toggleHistorial(alumnoId) {
    setHistorialAbierto(prev => prev === alumnoId ? null : alumnoId);
  }

  return (
    <section className="anotaciones__tabla-wrapper" aria-label="Listado de alumnos">
      <table className="anotaciones__tabla">
        <thead>
          <tr>
            <th scope="col">RUT</th>
            <th scope="col">Alumno</th>
            <th scope="col">Positivas</th>
            <th scope="col">Negativas</th>
            <th scope="col">Historial</th>
            <th scope="col">Acción</th>
          </tr>
        </thead>
        <tbody>
          {alumnos.map(alumno => {
            const anotaciones = anotacionesPorAlumno[alumno.id] || [];
            const positivas = anotaciones.filter(a => a.tipo === 'positiva').length;
            const negativas = anotaciones.filter(a => a.tipo === 'negativa').length;
            const abierto = panelActivo === alumno.id;

            return (
              <>
                <tr
                  key={alumno.id}
                  className={abierto ? 'anotaciones__fila--activa' : ''}
                >
                  <td className="anotaciones__celda-rut">{alumno.rut}</td>
                  <td className="anotaciones__celda-nombre">{alumno.nombre}</td>
                  <td>
                    <button
                      className="anotaciones__contador anotaciones__contador--positivo"
                      onClick={() => anotaciones.length > 0 && toggleHistorial(alumno.id)}
                      aria-expanded={historialAbierto === alumno.id}
                      title={positivas > 0 ? 'Ver anotaciones positivas' : undefined}
                      type="button"
                    >
                      <ThumbsUp size={12} aria-hidden="true" />
                      {positivas}
                    </button>
                  </td>
                  <td>
                    <button
                      className="anotaciones__contador anotaciones__contador--negativo"
                      onClick={() => anotaciones.length > 0 && toggleHistorial(alumno.id)}
                      aria-expanded={historialAbierto === alumno.id}
                      title={negativas > 0 ? 'Ver anotaciones negativas' : undefined}
                      type="button"
                    >
                      <ThumbsDown size={12} aria-hidden="true" />
                      {negativas}
                    </button>
                  </td>
                  <td>
                    {anotaciones.length > 0 ? (
                      <button
                        className="anotaciones__btn-historial"
                        onClick={() => toggleHistorial(alumno.id)}
                        aria-expanded={historialAbierto === alumno.id}
                        aria-label={`Ver historial de ${alumno.nombre}`}
                        type="button"
                      >
                        {historialAbierto === alumno.id
                          ? <ChevronUp size={14} aria-hidden="true" />
                          : <ChevronDown size={14} aria-hidden="true" />
                        }
                        {historialAbierto === alumno.id ? 'Ocultar' : 'Ver'}
                      </button>
                    ) : (
                      <span className="anotaciones__sin-datos">—</span>
                    )}
                  </td>
                  <td>
                    <button
                      className={`anotaciones__btn-toggle ${abierto ? 'anotaciones__btn-toggle--activo' : ''}`}
                      onClick={() => onTogglePanel(alumno.id)}
                      aria-expanded={abierto}
                      aria-label={abierto ? `Cerrar formulario de ${alumno.nombre}` : `Agregar anotación a ${alumno.nombre}`}
                    >
                      {abierto
                        ? <><X size={14} aria-hidden="true" /> Cerrar</>
                        : <><Plus size={14} aria-hidden="true" /> Agregar</>
                      }
                    </button>
                  </td>
                </tr>

                {historialAbierto === alumno.id && (
                  <tr key={`historial-${alumno.id}`} className="anotaciones__fila-historial">
                    <td colSpan={6}>
                      <ul className="anotaciones__historial-lista">
                        {anotaciones.map(a => (
                          <li key={a.id} className={`anotaciones__historial-item anotaciones__historial-item--${a.tipo}`}>
                            <span className="anotaciones__historial-tipo">
                              {a.tipo === 'positiva'
                                ? <ThumbsUp size={12} aria-hidden="true" />
                                : <ThumbsDown size={12} aria-hidden="true" />
                              }
                              {a.tipo}
                            </span>
                            <span className="anotaciones__historial-desc">{a.descripcion}</span>
                            {a.fecha && (
                              <span className="anotaciones__historial-fecha">
                                {new Date(a.fecha + 'T00:00:00').toLocaleDateString('es-CL', {
                                  day: '2-digit', month: 'short', year: 'numeric',
                                })}
                              </span>
                            )}
                          </li>
                        ))}
                      </ul>
                    </td>
                  </tr>
                )}

                {abierto && (
                  <tr key={`panel-${alumno.id}`} className="anotaciones__fila-panel">
                    <td colSpan={6}>
                      <form
                        className="anotaciones__panel"
                        onSubmit={e => onGuardar(e, alumno.id)}
                        noValidate
                      >
                        <p className="anotaciones__panel-titulo">
                          Nueva anotación para <strong>{alumno.nombre}</strong>
                        </p>

                        <div className="anotaciones__tipo-grupo">
                          <label className={`anotaciones__tipo-opcion ${formulario.tipo === 'positiva' ? 'anotaciones__tipo-opcion--activa anotaciones__tipo-opcion--positiva' : ''}`}>
                            <input
                              type="radio"
                              name="tipo"
                              value="positiva"
                              className="anotaciones__radio"
                              checked={formulario.tipo === 'positiva'}
                              onChange={() => onTipoChange('positiva')}
                            />
                            <ThumbsUp size={15} aria-hidden="true" />
                            Positiva
                          </label>
                          <label className={`anotaciones__tipo-opcion ${formulario.tipo === 'negativa' ? 'anotaciones__tipo-opcion--activa anotaciones__tipo-opcion--negativa' : ''}`}>
                            <input
                              type="radio"
                              name="tipo"
                              value="negativa"
                              className="anotaciones__radio"
                              checked={formulario.tipo === 'negativa'}
                              onChange={() => onTipoChange('negativa')}
                            />
                            <ThumbsDown size={15} aria-hidden="true" />
                            Negativa
                          </label>
                        </div>

                        <div className="anotaciones__campo">
                          <label htmlFor={`desc-${alumno.id}`} className="anotaciones__campo-label">
                            Descripción
                          </label>
                          <input
                            id={`desc-${alumno.id}`}
                            type="text"
                            className="anotaciones__input"
                            placeholder="Ej: Participación destacada en clase..."
                            value={formulario.descripcion}
                            onChange={onDescripcionChange}
                            required
                            autoFocus
                          />
                        </div>

                        <div className="anotaciones__panel-acciones">
                          <button type="submit" className="anotaciones__btn-guardar" disabled={guardadoDeshabilitado}>
                            Guardar anotación
                          </button>
                          <button
                            type="button"
                            className="anotaciones__btn-cancelar"
                            onClick={onCancelar}
                          >
                            Cancelar
                          </button>
                        </div>
                      </form>
                    </td>
                  </tr>
                )}
              </>
            );
          })}
        </tbody>
      </table>
    </section>
  );
}

export default TablaAnotaciones;
