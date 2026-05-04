import { CheckCircle, XCircle, FileText } from 'lucide-react';

const etiquetaEstado = { presente: 'Presente', ausente: 'Ausente', justificado: 'Justificado' };

function iconoEstado(estado) {
  if (estado === 'presente') return <CheckCircle size={15} aria-hidden="true" />;
  if (estado === 'ausente')  return <XCircle size={15} aria-hidden="true" />;
  return <FileText size={15} aria-hidden="true" />;
}

function TablaHistorialAsistencia({ registros }) {
  if (registros.length === 0) {
    return <p className="historial__vacio">Sin registros de asistencia para este alumno.</p>;
  }

  return (
    <section className="historial__tabla-wrapper" aria-label="Registros de asistencia">
      <table className="historial__tabla">
        <thead>
          <tr>
            <th scope="col">Fecha</th>
            <th scope="col">Estado</th>
            <th scope="col">Anotación</th>
          </tr>
        </thead>
        <tbody>
          {registros.map(registro => (
            <tr key={registro.id}>
              <td className="historial__celda-fecha">
                {new Date(registro.fecha + 'T00:00:00').toLocaleDateString('es-CL', {
                  weekday: 'short', day: '2-digit', month: 'short', year: 'numeric',
                })}
              </td>
              <td>
                <span className={`historial__badge historial__badge--${registro.estado}`}>
                  {iconoEstado(registro.estado)}
                  {etiquetaEstado[registro.estado]}
                </span>
              </td>
              <td className="historial__celda-anotacion">
                {registro.anotacion || <span className="historial__sin-anotacion">—</span>}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}

export default TablaHistorialAsistencia;
