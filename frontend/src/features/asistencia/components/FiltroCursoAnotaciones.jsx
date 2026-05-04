import { GraduationCap } from 'lucide-react';

function FiltroCursoAnotaciones({ cursos, cursoId, onChange }) {
  return (
    <div className="anotaciones__filtros">
      <div className="anotaciones__filtro-grupo">
        <label htmlFor="select-curso" className="anotaciones__label">
          <GraduationCap size={15} aria-hidden="true" />
          Curso
        </label>
        <select
          id="select-curso"
          className="anotaciones__select"
          value={cursoId}
          onChange={onChange}
        >
          {cursos.map(c => (
            <option key={c.id} value={c.id}>{c.nombre}</option>
          ))}
        </select>
      </div>
    </div>
  );
}

export default FiltroCursoAnotaciones;
