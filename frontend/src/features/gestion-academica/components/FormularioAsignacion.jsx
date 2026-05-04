import { Plus } from 'lucide-react';

function FormularioAsignacion({ formulario, docentes, cursos, asignaturas, onChange, onSubmit }) {
  return (
    <form className="asignacion__formulario" onSubmit={onSubmit} noValidate>
      <div className="asignacion__campos">
        <div className="asignacion__campo">
          <label htmlFor="select-docente" className="asignacion__label">Docente</label>
          <select
            id="select-docente"
            name="docenteId"
            className="asignacion__select"
            value={formulario.docenteId}
            onChange={onChange}
            required
          >
            <option value="">— Seleccionar —</option>
            {docentes.map(d => (
              <option key={d.id} value={d.id}>{d.nombre}</option>
            ))}
          </select>
        </div>

        <div className="asignacion__campo">
          <label htmlFor="select-curso" className="asignacion__label">Curso</label>
          <select
            id="select-curso"
            name="cursoId"
            className="asignacion__select"
            value={formulario.cursoId}
            onChange={onChange}
            required
          >
            <option value="">— Seleccionar —</option>
            {cursos.map(c => (
              <option key={c.id} value={c.id}>{c.nombre}</option>
            ))}
          </select>
        </div>

        <div className="asignacion__campo">
          <label htmlFor="select-asignatura" className="asignacion__label">Asignatura</label>
          <select
            id="select-asignatura"
            name="asignaturaId"
            className="asignacion__select"
            value={formulario.asignaturaId}
            onChange={onChange}
            required
          >
            <option value="">— Seleccionar —</option>
            {asignaturas.map(a => (
              <option key={a.id} value={a.id}>{a.nombre}</option>
            ))}
          </select>
        </div>
      </div>

      <button type="submit" className="asignacion__btn-agregar">
        <Plus size={16} aria-hidden="true" />
        Asignar
      </button>
    </form>
  );
}

export default FormularioAsignacion;
