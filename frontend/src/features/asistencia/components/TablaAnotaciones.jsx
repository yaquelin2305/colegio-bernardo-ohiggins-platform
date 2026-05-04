import { ThumbsUp, ThumbsDown, Plus, X } from 'lucide-react';

function TablaAnotaciones({ alumnos, anotacionesPorAlumno, panelActivo, formulario, onTogglePanel, onTipoChange, onDescripcionChange, onGuardar, onCancelar }) {
  return (
    <section className="anotaciones__tabla-wrapper" aria-label="Listado de alumnos">
      <table className="anotaciones__tabla">
        <thead>
          <tr>
            <th scope="col">RUT</th>
            <th scope="col">Alumno</th>
            <th scope="col">Positivas</th>
            <th scope="col">Negativas</th>
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
                    <span className="anotaciones__contador anotaciones__contador--positivo">
                      <ThumbsUp size={12} aria-hidden="true" />
                      {positivas}
                    </span>
                  </td>
                  <td>
                    <span className="anotaciones__contador anotaciones__contador--negativo">
                      <ThumbsDown size={12} aria-hidden="true" />
                      {negativas}
                    </span>
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

                {abierto && (
                  <tr key={`panel-${alumno.id}`} className="anotaciones__fila-panel">
                    <td colSpan={5}>
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
                          <button type="submit" className="anotaciones__btn-guardar">
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
