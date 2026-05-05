import { useState } from 'react';
import { Search } from 'lucide-react';
import '../styles/FiltroAsistencia.css';

function FiltroAsistencia({ cursos = [], onFiltrar }) {
  const [curso, setCurso] = useState('');
  const [fecha, setFecha] = useState(new Date().toISOString().split('T')[0]);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (onFiltrar) {
      onFiltrar({ curso, fecha });
    }
  };

  return (
    <section className="filtro-asistencia" aria-label="Filtros de asistencia">
      <h2 className="filtro-asistencia__title">Filtrar Asistencia</h2>
      <form className="filtro-asistencia__form" onSubmit={handleSubmit}>
        <div className="filtro-asistencia__grupo">
          <label className="filtro-asistencia__label" htmlFor="curso">Curso</label>
          <select
            id="curso"
            className="filtro-asistencia__select"
            value={curso}
            onChange={(e) => setCurso(e.target.value)}
          >
            <option value="">Seleccionar curso</option>
            {cursos.map((c) => (
              <option key={c.id} value={c.id}>
                {c.nombre}
              </option>
            ))}
          </select>
        </div>
        <div className="filtro-asistencia__grupo">
          <label className="filtro-asistencia__label" htmlFor="fecha">Fecha</label>
          <input
            id="fecha"
            type="date"
            className="filtro-asistencia__input"
            value={fecha}
            onChange={(e) => setFecha(e.target.value)}
          />
        </div>
        <button type="submit" className="filtro-asistencia__btn">
          <Search size={18} />
          Filtrar
        </button>
      </form>
    </section>
  );
}

export default FiltroAsistencia;
