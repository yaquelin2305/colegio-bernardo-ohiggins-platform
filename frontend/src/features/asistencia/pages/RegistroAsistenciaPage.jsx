import { useState } from 'react';
import { GraduationCap, CalendarDays, Save } from 'lucide-react';
import MainLayout from '../../../shared/components/layout/MainLayout';
import '../styles/RegistroAsistenciaPage.css';

const estadosAsistencia = [
  { valor: 'presente', etiqueta: 'Presente' },
  { valor: 'ausente',  etiqueta: 'Ausente'  },
  { valor: 'tardanza', etiqueta: 'Tardanza' },
];

function RegistroAsistenciaPage() {
  const [curso, setCurso]     = useState('1M-A');
  const [fecha, setFecha]     = useState(new Date().toISOString().split('T')[0]);
  const [alumnos, setAlumnos] = useState([]);

  const presentes = alumnos.filter(a => a.estado === 'presente').length;
  const ausentes  = alumnos.filter(a => a.estado === 'ausente').length;
  const tardanzas = alumnos.filter(a => a.estado === 'tardanza').length;

  function handleEstadoChange(id, nuevoEstado) {
    setAlumnos(prev =>
      prev.map(a => a.id === id ? { ...a, estado: nuevoEstado } : a)
    );
  }

  function handleObservacionChange(id, texto) {
    setAlumnos(prev =>
      prev.map(a => a.id === id ? { ...a, observacion: texto } : a)
    );
  }

  function handleGuardar() {
    console.log('Asistencia guardada:', { curso, fecha, alumnos });
  }

  return (
    <MainLayout titulo="Registro de Asistencia">
      <div className="registro-asistencia">

        <section className="registro-asistencia__filtros" aria-label="Filtros de curso y fecha">
          <div className="registro-asistencia__filtro-grupo">
            <label htmlFor="select-curso" className="registro-asistencia__label">
              <GraduationCap size={16} aria-hidden="true" />
              Curso
            </label>
            <select
              id="select-curso"
              className="registro-asistencia__select"
              value={curso}
              onChange={e => setCurso(e.target.value)}
            >
              <option value="1M-A">1° Medio A</option>
              <option value="1M-B">1° Medio B</option>
              <option value="2M-A">2° Medio A</option>
              <option value="2M-B">2° Medio B</option>
              <option value="3M-A">3° Medio A</option>
              <option value="3M-B">3° Medio B</option>
              <option value="4M-A">4° Medio A</option>
            </select>
          </div>

          <div className="registro-asistencia__filtro-grupo">
            <label htmlFor="input-fecha" className="registro-asistencia__label">
              <CalendarDays size={16} aria-hidden="true" />
              Fecha
            </label>
            <input
              id="input-fecha"
              type="date"
              className="registro-asistencia__select"
              value={fecha}
              onChange={e => setFecha(e.target.value)}
            />
          </div>

          {alumnos.length > 0 && (
            <div className="registro-asistencia__resumen" aria-label="Resumen de asistencia">
              <span className="resumen__item resumen__item--presente">{presentes} presentes</span>
              <span className="resumen__item resumen__item--ausente">{ausentes} ausentes</span>
              <span className="resumen__item resumen__item--tardanza">{tardanzas} tardanzas</span>
            </div>
          )}
        </section>

        <section className="registro-asistencia__tabla-wrapper" aria-label="Lista de asistencia">
          {alumnos.length === 0 ? (
            <p className="registro-asistencia__vacio">No hay alumnos cargados para este curso.</p>
          ) : (
            <table className="registro-asistencia__tabla">
              <thead>
                <tr>
                  <th scope="col">RUT</th>
                  <th scope="col">Nombre Alumno</th>
                  {estadosAsistencia.map(e => (
                    <th key={e.valor} scope="col">{e.etiqueta}</th>
                  ))}
                  <th scope="col">Observación</th>
                </tr>
              </thead>
              <tbody>
                {alumnos.map(alumno => (
                  <tr key={alumno.id} className={`fila--${alumno.estado}`}>
                    <td className="registro-asistencia__rut">{alumno.rut}</td>
                    <td className="registro-asistencia__nombre">{alumno.nombre}</td>
                    {estadosAsistencia.map(e => (
                      <td key={e.valor} className="registro-asistencia__celda-radio">
                        <input
                          type="radio"
                          name={`asistencia-${alumno.id}`}
                          value={e.valor}
                          checked={alumno.estado === e.valor}
                          onChange={() => handleEstadoChange(alumno.id, e.valor)}
                          aria-label={`${e.etiqueta} para ${alumno.nombre}`}
                          className="registro-asistencia__radio"
                        />
                      </td>
                    ))}
                    <td>
                      <input
                        type="text"
                        className="registro-asistencia__input-obs"
                        value={alumno.observacion}
                        placeholder="Sin observación"
                        aria-label={`Observación para ${alumno.nombre}`}
                        onChange={e => handleObservacionChange(alumno.id, e.target.value)}
                      />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </section>

        {alumnos.length > 0 && (
          <footer className="registro-asistencia__acciones">
            <button className="registro-asistencia__btn-guardar" onClick={handleGuardar}>
              <Save size={18} aria-hidden="true" />
              Guardar Asistencia
            </button>
          </footer>
        )}

      </div>
    </MainLayout>
  );
}

export default RegistroAsistenciaPage;
