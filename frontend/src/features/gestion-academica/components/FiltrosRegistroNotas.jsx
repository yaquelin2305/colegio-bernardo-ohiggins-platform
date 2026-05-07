import { BookOpen, GraduationCap } from 'lucide-react';

function FiltrosRegistroNotas({ curso, asignatura, cursos, asignaturas, onCursoChange, onAsignaturaChange }) {
  return (
    <section className="registro-notas__filtros" aria-label="Filtros de curso y asignatura">
      <div className="registro-notas__filtro-grupo">
        <label htmlFor="select-curso" className="registro-notas__label">
          <GraduationCap size={16} aria-hidden="true" />
          Curso
        </label>
        <select
          id="select-curso"
          className="registro-notas__select"
          value={curso}
          onChange={onCursoChange}
        >
          {cursos.map(c => (
            <option key={c.id} value={c.id}>{c.nombre}</option>
          ))}
        </select>
      </div>

      <div className="registro-notas__filtro-grupo">
        <label htmlFor="select-asignatura" className="registro-notas__label">
          <BookOpen size={16} aria-hidden="true" />
          Asignatura
        </label>
        <select
          id="select-asignatura"
          className="registro-notas__select"
          value={asignatura}
          onChange={onAsignaturaChange}
        >
          {asignaturas.map(a => (
            <option key={a.id} value={a.id}>{a.nombre}</option>
          ))}
        </select>
      </div>
    </section>
  );
}

export default FiltrosRegistroNotas;
