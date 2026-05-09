import '../styles/AsistenciaTable.css';

const ESTADOS = ['presente', 'ausente', 'justificado'];
const ESTADO_LABELS = { presente: 'Presente', ausente: 'Ausente', justificado: 'Justificado' };

function AsistenciaTable({ estudiantes = [], onCambiarEstado }) {
  if (!estudiantes.length) {
    return (
      <div className="asistencia-table-container">
        <p className="asistencia-table__empty">No hay registros de asistencia para los filtros seleccionados.</p>
      </div>
    );
  }

  return (
    <div className="asistencia-table-container">
      <table className="asistencia-table" role="table" aria-label="Lista de asistencia">
        <thead>
          <tr>
            <th scope="col">N°</th>
            <th scope="col">Nombre Estudiante</th>
            <th scope="col">Curso</th>
            <th scope="col">Estado</th>
            <th scope="col">Hora</th>
          </tr>
        </thead>
        <tbody>
          {estudiantes.map((estudiante, index) => (
            <tr key={estudiante.id}>
              <td className="asistencia-table__numero">{index + 1}</td>
              <td className="asistencia-table__nombre">{estudiante.nombre}</td>
              <td>
                <span className="asistencia-table__curso">{estudiante.curso}</span>
              </td>
              <td>
                <select
                  className={`asistencia-table__select asistencia-table__select--${estudiante.estado}`}
                  value={estudiante.estado}
                  onChange={(e) => onCambiarEstado(estudiante.id, e.target.value)}
                  aria-label={`Estado de asistencia de ${estudiante.nombre}`}
                >
                  {ESTADOS.map((estado) => (
                    <option key={estado} value={estado}>{ESTADO_LABELS[estado]}</option>
                  ))}
                </select>
              </td>
              <td className="asistencia-table__hora">{estudiante.hora}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AsistenciaTable;
