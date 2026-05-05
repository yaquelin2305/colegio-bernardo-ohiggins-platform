import { useState, useEffect } from 'react';
import { useParams, useNavigate, useOutletContext } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import InfoCurso from '../components/InfoCurso';
import PanelMatricula from '../components/PanelMatricula';
import TablaEstudiantes from '../components/TablaEstudiantes';
import {
  obtenerCursoPorId,
  obtenerEstudiantesPorCurso,
  obtenerEstudiantesDisponibles,
  matricularEstudiante,
} from '../services/gestionAcademicaService';
import '../styles/ListadoEstudiantesCursoPage.css';

function ListadoEstudiantesCursoPage() {
  const { setTitulo } = useOutletContext();
  const { cursoId } = useParams();
  const navigate = useNavigate();

  useEffect(() => { setTitulo('Listado de Estudiantes'); }, [setTitulo]);

  const [curso, setCurso] = useState(null);
  const [estudiantes, setEstudiantes] = useState([]);
  const [disponibles, setDisponibles] = useState([]);
  const [mostrarPanel, setMostrarPanel] = useState(false);
  const [alumnoSeleccionadoId, setAlumnoSeleccionadoId] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    Promise.all([
      obtenerCursoPorId(cursoId),
      obtenerEstudiantesPorCurso(cursoId),
      obtenerEstudiantesDisponibles(),
    ])
      .then(([datosCurso, datosEstudiantes, datosDisponibles]) => {
        setCurso(datosCurso);
        setEstudiantes(datosEstudiantes);
        setDisponibles(datosDisponibles);
      })
      .catch(() => setError('No se pudo cargar la información del curso.'))
      .finally(() => setIsLoading(false));
  }, [cursoId]);

  const disponiblesFiltrados = disponibles.filter(
    d => !estudiantes.some(e => e.id === d.id)
  );

  async function handleMatricular(e) {
    e.preventDefault();
    if (!alumnoSeleccionadoId) return;

    const alumno = disponiblesFiltrados.find(d => d.id === alumnoSeleccionadoId);
    if (!alumno) return;

    try {
      await matricularEstudiante(cursoId, alumnoSeleccionadoId);
      setEstudiantes(prev => [...prev, alumno]);
      setAlumnoSeleccionadoId('');
      setMostrarPanel(false);
    } catch {
      setError('No se pudo realizar la matrícula. Intenta nuevamente.');
    }
  }

  if (isLoading) {
    return <p className="listado-estudiantes__cargando">Cargando...</p>;
  }

  if (error || !curso) {
    return (
      <div className="listado-estudiantes__no-encontrado">
        <p>{error ?? 'El curso solicitado no existe.'}</p>
        <button className="listado-estudiantes__btn-volver" onClick={() => navigate(-1)}>
          <ArrowLeft size={16} aria-hidden="true" />
          Volver
        </button>
      </div>
    );
  }

  return (
    <div className="listado-estudiantes">

      <InfoCurso
        curso={curso}
        totalEstudiantes={estudiantes.length}
        mostrarPanel={mostrarPanel}
        onTogglePanel={() => setMostrarPanel(prev => !prev)}
      />

      {mostrarPanel && (
        <PanelMatricula
          curso={curso}
          disponibles={disponiblesFiltrados}
          alumnoSeleccionadoId={alumnoSeleccionadoId}
          onAlumnoChange={e => setAlumnoSeleccionadoId(e.target.value)}
          onMatricular={handleMatricular}
          onCancelar={() => setMostrarPanel(false)}
        />
      )}

      <section className="listado-estudiantes__tabla-wrapper" aria-label="Nómina de estudiantes">
        <TablaEstudiantes estudiantes={estudiantes} />
      </section>

    </div>
  );
}

export default ListadoEstudiantesCursoPage;
