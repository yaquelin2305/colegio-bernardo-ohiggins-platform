import { AlertTriangle, FileCheck, Upload, X } from 'lucide-react';

function TablaInasistenciasPendientes({ pendientes, formularioActivo, formulario, onAbrir, onCerrar, onChange, onJustificar }) {
  return (
    <section className="justificacion__seccion" aria-label="Inasistencias sin justificar">
      <h2 className="justificacion__titulo-seccion">
        <AlertTriangle size={16} aria-hidden="true" />
        Inasistencias por justificar
      </h2>

      {pendientes.length === 0 ? (
        <p className="justificacion__vacio">No hay inasistencias pendientes.</p>
      ) : (
        <table className="justificacion__tabla">
          <thead>
            <tr>
              <th scope="col">Fecha</th>
              <th scope="col">Alumno</th>
              <th scope="col">Curso</th>
              <th scope="col">Acción</th>
            </tr>
          </thead>
          <tbody>
            {pendientes.map(inasistencia => (
              <>
                <tr key={inasistencia.id}>
                  <td>
                    {new Date(inasistencia.fecha + 'T00:00:00').toLocaleDateString('es-CL', {
                      day: '2-digit', month: 'short', year: 'numeric',
                    })}
                  </td>
                  <td>{inasistencia.alumno}</td>
                  <td>{inasistencia.curso}</td>
                  <td>
                    {formularioActivo === inasistencia.id ? (
                      <button
                        className="justificacion__btn-cancelar-fila"
                        onClick={onCerrar}
                        aria-label="Cancelar"
                      >
                        <X size={13} aria-hidden="true" />
                        Cancelar
                      </button>
                    ) : (
                      <button
                        className="justificacion__btn-justificar"
                        onClick={() => onAbrir(inasistencia.id)}
                        aria-label={`Justificar inasistencia del ${inasistencia.fecha}`}
                      >
                        <FileCheck size={14} aria-hidden="true" />
                        Justificar
                      </button>
                    )}
                  </td>
                </tr>

                {formularioActivo === inasistencia.id && (
                  <tr key={`form-${inasistencia.id}`} className="justificacion__fila-formulario">
                    <td colSpan={4}>
                      <form className="justificacion__formulario" onSubmit={onJustificar} noValidate>
                        <div className="justificacion__campo">
                          <label htmlFor="motivo" className="justificacion__campo-label">
                            Motivo de la inasistencia
                          </label>
                          <textarea
                            id="motivo"
                            name="motivo"
                            className="justificacion__textarea"
                            placeholder="Describe el motivo de la inasistencia..."
                            rows={3}
                            value={formulario.motivo}
                            onChange={onChange}
                            required
                            autoFocus
                          />
                        </div>
                        <div className="justificacion__campo">
                          <label htmlFor="archivo" className="justificacion__campo-label">
                            <Upload size={13} aria-hidden="true" />
                            Adjuntar documento (opcional)
                          </label>
                          <input
                            id="archivo"
                            name="archivo"
                            type="file"
                            className="justificacion__input-archivo"
                            accept=".pdf,.jpg,.jpeg,.png"
                            onChange={onChange}
                          />
                        </div>
                        <div className="justificacion__acciones">
                          <button type="submit" className="justificacion__btn-enviar">
                            <FileCheck size={15} aria-hidden="true" />
                            Enviar justificación
                          </button>
                        </div>
                      </form>
                    </td>
                  </tr>
                )}
              </>
            ))}
          </tbody>
        </table>
      )}
    </section>
  );
}

export default TablaInasistenciasPendientes;
