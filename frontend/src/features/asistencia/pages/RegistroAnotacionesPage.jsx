import { useState, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import FiltroCursoAnotaciones from '../components/FiltroCursoAnotaciones';
import TablaAnotaciones from '../components/TablaAnotaciones';
import { obtenerCursos, obtenerAlumnosPorCurso, guardarAnotacion, obtenerAnotacionesPorEstudiante } from '../services/asistenciaService';
import { useToast } from '../../../shared/hooks/useToast';
import Toast from '../../../shared/components/ui/Toast';
import '../styles/RegistroAnotacionesPage.css';

const formularioInicial = { tipo: 'positiva', descripcion: '' };

function RegistroAnotacionesPage() {
  const { setTitulo } = useOutletContext();
  const { toast, showToast } = useToast();
  const [cursos, setCursos] = useState([]);

  useEffect(() => { setTitulo('Registro de Anotaciones'); }, [setTitulo]);
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
    if (!cursoId) return;
    obtenerAlumnosPorCurso(cursoId)
      .then(async (data) => {
        setAnotacionesPorAlumno({});
        setPanelActivo(null);
        setAlumnos(data);
        const mapaAnotaciones = {};
        await Promise.all(data.map(async (alumno) => {
          try {
            mapaAnotaciones[alumno.id] = await obtenerAnotacionesPorEstudiante(alumno.id);
          } catch {
            mapaAnotaciones[alumno.id] = [];
          }
        }));
        setAnotacionesPorAlumno(mapaAnotaciones);
      })
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
    try {
      const guardada = await guardarAnotacion(alumnoId, { tipo: formulario.tipo, descripcion: formulario.descripcion });
      setAnotacionesPorAlumno(prev => ({
        ...prev,
        [alumnoId]: [...(prev[alumnoId] || []), guardada],
      }));
      setPanelActivo(null);
      showToast('Anotación guardada correctamente.');
    } catch {
      showToast('No se pudo guardar la anotación.', 'error');
    }
  }

  return (
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

      <Toast toast={toast} onClose={() => {}} />
    </div>
  );
}

export default RegistroAnotacionesPage;
