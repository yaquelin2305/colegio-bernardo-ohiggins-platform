import { FileCheck } from 'lucide-react';

function TablaInasistenciasJustificadas({ justificadas }) {
  if (justificadas.length === 0) return null;

  return (
    <section className="justificacion__seccion" aria-label="Inasistencias justificadas">
      <h2 className="justificacion__titulo-seccion">
        <FileCheck size={16} aria-hidden="true" />
        Justificadas
      </h2>
      <table className="justificacion__tabla">
        <thead>
          <tr>
            <th scope="col">Fecha</th>
            <th scope="col">Alumno</th>
            <th scope="col">Curso</th>
            <th scope="col">Estado</th>
          </tr>
        </thead>
        <tbody>
          {justificadas.map(inasistencia => (
            <tr key={inasistencia.id} className="justificacion__fila-justificada">
              <td>
                {new Date(inasistencia.fecha + 'T00:00:00').toLocaleDateString('es-CL', {
                  day: '2-digit', month: 'short', year: 'numeric',
                })}
              </td>
              <td>{inasistencia.alumno}</td>
              <td>{inasistencia.curso}</td>
              <td>
                <span className="justificacion__badge-ok">
                  <FileCheck size={13} aria-hidden="true" />
                  Justificada
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </section>
  );
}

export default TablaInasistenciasJustificadas;
