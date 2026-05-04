import { BookOpen, GraduationCap } from 'lucide-react';

function FiltrosRegistroNotas({ curso, asignatura, onCursoChange, onAsignaturaChange }) {
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
          <option value="1M-A">1° Medio A</option>
          <option value="1M-B">1° Medio B</option>
          <option value="2M-A">2° Medio A</option>
          <option value="2M-B">2° Medio B</option>
          <option value="3M-A">3° Medio A</option>
          <option value="4M-A">4° Medio A</option>
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
          <option value="matematicas">Matemáticas</option>
          <option value="lenguaje">Lenguaje y Comunicación</option>
          <option value="historia">Historia</option>
          <option value="ciencias">Ciencias Naturales</option>
          <option value="ingles">Inglés</option>
        </select>
      </div>
    </section>
  );
}

export default FiltrosRegistroNotas;
