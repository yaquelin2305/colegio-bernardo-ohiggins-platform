import { useState } from 'react';
import { BookOpen, GraduationCap, Save } from 'lucide-react';
import MainLayout from '../../../shared/components/layout/MainLayout';
import '../styles/RegistroNotasPage.css';

const alumnosIniciales = [];

function calcularPromedio(nota1, nota2, nota3) {
  const notas = [nota1, nota2, nota3].map(Number).filter(n => !isNaN(n));
  if (notas.length === 0) return 0;
  return Math.round((notas.reduce((a, b) => a + b, 0) / notas.length) * 10) / 10;
}

function clasificarPromedio(promedio) {
  if (promedio >= 6.0) return 'nota--alta';
  if (promedio >= 4.0) return 'nota--media';
  return 'nota--baja';
}

function RegistroNotasPage() {
  const [curso, setCurso] = useState('1M-A');
  const [asignatura, setAsignatura] = useState('matematicas');
  const [alumnos, setAlumnos] = useState(alumnosIniciales);

  function handleNotaChange(id, campo, valor) {
    setAlumnos(prev =>
      prev.map(alumno => {
        if (alumno.id !== id) return alumno;
        const actualizado = { ...alumno, [campo]: valor };
        actualizado.promedio = calcularPromedio(
          actualizado.nota1,
          actualizado.nota2,
          actualizado.nota3
        );
        return actualizado;
      })
    );
  }

  function handleGuardar() {
    console.log('Calificaciones guardadas:', { curso, asignatura, alumnos });
  }

  return (
    <MainLayout titulo="Registro de Calificaciones">
      <div className="registro-notas">

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
              onChange={e => setCurso(e.target.value)}
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
              onChange={e => setAsignatura(e.target.value)}
            >
              <option value="matematicas">Matemáticas</option>
              <option value="lenguaje">Lenguaje y Comunicación</option>
              <option value="historia">Historia</option>
              <option value="ciencias">Ciencias Naturales</option>
              <option value="ingles">Inglés</option>
            </select>
          </div>
        </section>

        <section className="registro-notas__tabla-wrapper" aria-label="Tabla de calificaciones">
          <table className="registro-notas__tabla">
            <thead>
              <tr>
                <th scope="col">RUT</th>
                <th scope="col">Nombre Alumno</th>
                <th scope="col">Nota 1</th>
                <th scope="col">Nota 2</th>
                <th scope="col">Nota 3</th>
                <th scope="col">Promedio</th>
              </tr>
            </thead>
            <tbody>
              {alumnos.map(alumno => (
                <tr key={alumno.id}>
                  <td className="registro-notas__rut">{alumno.rut}</td>
                  <td className="registro-notas__nombre">{alumno.nombre}</td>
                  {['nota1', 'nota2', 'nota3'].map(campo => (
                    <td key={campo} className="registro-notas__celda-nota">
                      <input
                        type="number"
                        className="registro-notas__input-nota"
                        min="1"
                        max="7"
                        step="0.1"
                        value={alumno[campo]}
                        aria-label={`${campo.replace('nota', 'Nota ')} de ${alumno.nombre}`}
                        onChange={e => handleNotaChange(alumno.id, campo, e.target.value)}
                      />
                    </td>
                  ))}
                  <td className={`registro-notas__promedio ${clasificarPromedio(alumno.promedio)}`}>
                    {alumno.promedio.toFixed(1)}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </section>

        <footer className="registro-notas__acciones">
          <button className="registro-notas__btn-guardar" onClick={handleGuardar}>
            <Save size={18} aria-hidden="true" />
            Guardar Calificaciones
          </button>
        </footer>

      </div>
    </MainLayout>
  );
}

export default RegistroNotasPage;
