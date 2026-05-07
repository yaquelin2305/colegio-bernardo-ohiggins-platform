import { CheckCircle, XCircle, Clock3, Pencil } from 'lucide-react';
import '../styles/AsistenciaTable.css';

const ESTADOS = {
  presente: { label: 'Presente', icon: CheckCircle, class: 'asistencia-table__estado--presente' },
  ausente: { label: 'Ausente', icon: XCircle, class: 'asistencia-table__estado--ausente' },
  atrasado: { label: 'Atrasado', icon: Clock3, class: 'asistencia-table__estado--atrasado' },
};

function AsistenciaTable({ estudiantes = [] }) {
  const renderEstado = (estado) => {
    const config = ESTADOS[estado];
    const Icon = config.icon;
    return (
      <span className={`asistencia-table__estado ${config.class}`}>
        <Icon size={14} />
        {config.label}
      </span>
    );
  };

  const handleEditar = (id) => {
    console.log('Editar asistencia del estudiante:', id);
  };

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
            <th scope="col">Acción</th>
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
              <td>{renderEstado(estudiante.estado)}</td>
              <td className="asistencia-table__hora">{estudiante.hora}</td>
              <td className="asistencia-table__action">
                <button
                  className="asistencia-table__btn"
                  onClick={() => handleEditar(estudiante.id)}
                  aria-label={`Editar asistencia de ${estudiante.nombre}`}
                >
                  <Pencil size={16} />
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default AsistenciaTable;
