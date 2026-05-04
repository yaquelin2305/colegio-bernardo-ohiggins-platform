import { useState, useEffect } from 'react';
import MainLayout from '../../../shared/components/layout/MainLayout';
import FiltroCursoAnotaciones from '../components/FiltroCursoAnotaciones';
import TablaAnotaciones from '../components/TablaAnotaciones';
import { obtenerCursos, obtenerAlumnosPorCurso, guardarAnotacion } from '../services/asistenciaService';
import '../styles/RegistroAnotacionesPage.css';

const formularioInicial = { tipo: 'positiva', descripcion: '' };

function RegistroAnotacionesPage() {
  const [cursos, setCursos] = useState([]);
  const [cursoId, setCursoId] = useState('');
  const [alumnos, setAlumnos] = useState([]);
  const [anotacionesPorAlumno, setAnotacionesPorAlumno] = useState({});
  const [panelActivo, setPanelActivo] = useState(null);
  const [formulario, setFormulario] = useState(formularioInicial);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    obtenerCursos()
      .then(data => {
        setCursos(data);
        if (data.length > 0) setCursoId(data[0].id);
      })
      .catch(() => setError('No se pudo cargar el listado de cursos.'))
      .finally(() => setIsLoading(false));
  }, []);

  useEffect(() => {
    if (!cursoId) {
      setAlumnos([]);
      return;
    }
    setAnotacionesPorAlumno({});
    setPanelActivo(null);
    obtenerAlumnosPorCurso(cursoId)
      .then(setAlumnos)
      .catch(() => setError('No se pudo cargar los alumnos del curso.'));
  }, [cursoId]);

  function handleCursoChange(e) {
    setCursoId(e.target.value);
  }

  function handleTogglePanel(alumnoId) {
    if (panelActivo === alumnoId) {
      setPanelActivo(null);
    } else {
      setPanelActivo(alumnoId);
      setFormulario(formularioInicial);
    }
  }

  async function handleGuardar(e, alumnoId) {
    e.preventDefault();
    if (!formulario.descripcion.trim()) return;
    const nueva = { id: Date.now(), tipo: formulario.tipo, descripcion: formulario.descripcion };
    await guardarAnotacion(alumnoId, nueva);
    setAnotacionesPorAlumno(prev => ({
      ...prev,
      [alumnoId]: [...(prev[alumnoId] || []), nueva],
    }));
    setPanelActivo(null);
  }

  return (
    <MainLayout titulo="Registro de Anotaciones">
      <div className="anotaciones">

        {isLoading && <p className="anotaciones__cargando">Cargando...</p>}
        {error && <p className="anotaciones__error">{error}</p>}

        {!isLoading && !error && (
          <>
            <FiltroCursoAnotaciones
              cursos={cursos}
              cursoId={cursoId}
              onChange={handleCursoChange}
            />

            <TablaAnotaciones
              alumnos={alumnos}
              anotacionesPorAlumno={anotacionesPorAlumno}
              panelActivo={panelActivo}
              formulario={formulario}
              onTogglePanel={handleTogglePanel}
              onTipoChange={tipo => setFormulario(prev => ({ ...prev, tipo }))}
              onDescripcionChange={e => setFormulario(prev => ({ ...prev, descripcion: e.target.value }))}
              onGuardar={handleGuardar}
              onCancelar={() => setPanelActivo(null)}
            />
          </>
        )}

      </div>
    </MainLayout>
  );
}

export default RegistroAnotacionesPage;
