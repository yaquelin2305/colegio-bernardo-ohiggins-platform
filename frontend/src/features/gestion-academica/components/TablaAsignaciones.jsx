import { Trash2 } from 'lucide-react';

function TablaAsignaciones({ asignaciones, onEliminar }) {
  return (
    <table className="asignacion__tabla">
      <thead>
        <tr>
          <th scope="col">Docente</th>
          <th scope="col">Curso</th>
          <th scope="col">Asignatura</th>
          <th scope="col">Acciones</th>
        </tr>
      </thead>
      <tbody>
        {asignaciones.length === 0 && (
          <tr>
            <td colSpan={4} className="asignacion__tabla-vacia">
              No hay asignaciones registradas.
            </td>
          </tr>
        )}
        {asignaciones.map(a => (
          <tr key={a.id}>
            <td>{a.docente.nombre}</td>
            <td>{a.curso.nombre}</td>
            <td>{a.asignatura.nombre}</td>
            <td>
              <button
                className="asignacion__btn-eliminar"
                onClick={() => onEliminar(a.id)}
                aria-label={`Eliminar asignación de ${a.docente.nombre} en ${a.asignatura.nombre}`}
              >
                <Trash2 size={15} />
              </button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}

export default TablaAsignaciones;
