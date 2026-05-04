import { Users } from 'lucide-react';

function FiltroAlumno({ alumnos, alumnoId, onChange }) {
  return (
    <div className="historial__filtros">
      <div className="historial__filtro-grupo">
        <label htmlFor="select-alumno" className="historial__label">
          <Users size={15} aria-hidden="true" />
          Alumno
        </label>
        <select
          id="select-alumno"
          className="historial__select"
          value={alumnoId}
          onChange={onChange}
        >
          <option value="">— Selecciona un alumno —</option>
          {alumnos.map(a => (
            <option key={a.id} value={a.id}>
              {a.nombre} ({a.curso})
            </option>
          ))}
        </select>
      </div>
    </div>
  );
}

export default FiltroAlumno;
