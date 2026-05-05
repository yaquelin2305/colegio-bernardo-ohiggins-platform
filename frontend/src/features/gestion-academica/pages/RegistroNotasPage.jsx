import { useState, useEffect } from 'react';
import { Save } from 'lucide-react';
import { useOutletContext } from 'react-router-dom';
import FiltrosRegistroNotas from '../components/FiltrosRegistroNotas';
import TablaCalificaciones from '../components/TablaCalificaciones';
import { obtenerCalificaciones, guardarCalificaciones, obtenerCursos, obtenerAsignaturas } from '../services/gestionAcademicaService';
import '../styles/RegistroNotasPage.css';

function calcularPromedio(nota1, nota2, nota3) {
  const notas = [nota1, nota2, nota3].map(Number).filter(n => !isNaN(n));
  if (notas.length === 0) return 0;
  return Math.round((notas.reduce((a, b) => a + b, 0) / notas.length) * 10) / 10;
}

function RegistroNotasPage() {
  const { setTitulo } = useOutletContext();
  const [curso, setCurso] = useState('');
  const [asignatura, setAsignatura] = useState('');
  const [cursos, setCursos] = useState([]);
  const [asignaturas, setAsignaturas] = useState([]);

  useEffect(() => { setTitulo('Registro de Calificaciones'); }, [setTitulo]);
  const [alumnos, setAlumnos] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    Promise.all([obtenerCursos(), obtenerAsignaturas()])
      .then(([datosCursos, datosAsignaturas]) => {
        setCursos(datosCursos);
        setAsignaturas(datosAsignaturas);
        if (datosCursos.length > 0) setCurso(datosCursos[0].id);
        if (datosAsignaturas.length > 0) setAsignatura(datosAsignaturas[0].id);
      })
      .catch(() => setError('No se pudo cargar cursos o asignaturas.'));
  }, []);

  useEffect(() => {
    if (!curso || !asignatura) return;
    setIsLoading(true);
    setError(null);
    obtenerCalificaciones(curso, asignatura)
      .then(setAlumnos)
      .catch(() => setError('No se pudo cargar el listado de calificaciones.'))
      .finally(() => setIsLoading(false));
  }, [curso, asignatura]);

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

  async function handleGuardar() {
    try {
      await guardarCalificaciones(curso, asignatura, alumnos);
    } catch {
      setError('No se pudieron guardar las calificaciones. Intenta nuevamente.');
    }
  }

  return (
    <div className="registro-notas">
      <FiltrosRegistroNotas
        curso={curso}
        asignatura={asignatura}
        cursos={cursos}
        asignaturas={asignaturas}
        onCursoChange={e => setCurso(e.target.value)}
        onAsignaturaChange={e => setAsignatura(e.target.value)}
      />

      {isLoading && <p className="registro-notas__cargando">Cargando...</p>}
      {error && <p className="registro-notas__error">{error}</p>}

      {!isLoading && !error && (
        <TablaCalificaciones alumnos={alumnos} onNotaChange={handleNotaChange} />
      )}

      <footer className="registro-notas__acciones">
        <button
          className="registro-notas__btn-guardar"
          onClick={handleGuardar}
          disabled={isLoading}
        >
          <Save size={18} aria-hidden="true" />
          Guardar Calificaciones
        </button>
      </footer>
    </div>
  );
}

export default RegistroNotasPage;
