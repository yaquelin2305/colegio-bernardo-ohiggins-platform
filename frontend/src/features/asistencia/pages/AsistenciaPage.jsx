import { useState, useEffect } from 'react';
import { ClipboardCheck } from 'lucide-react';
import { useOutletContext } from 'react-router-dom';
import ResumenAsistencia from '../components/ResumenAsistencia';
import FiltroAsistencia from '../components/FiltroAsistencia';
import AsistenciaTable from '../components/AsistenciaTable';
import {
  obtenerCursos,
  obtenerResumenDiario,
  obtenerAsistenciaPorCurso,
  obtenerAlumnosPorCurso,
  guardarAsistencia,
} from '../services/asistenciaService';
import { useToast } from '../../../shared/hooks/useToast';
import Toast from '../../../shared/components/ui/Toast';
import '../styles/AsistenciaPage.css';

function AsistenciaPage() {
  const { setTitulo } = useOutletContext();
  const [cursos, setCursos] = useState([]);

  useEffect(() => { setTitulo('Toma de Asistencia'); }, [setTitulo]);
  const [resumen, setResumen] = useState(null);
  const [estudiantes, setEstudiantes] = useState([]);
  const [filtroActual, setFiltroActual] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState('');
  const { toast, showToast } = useToast();

  useEffect(() => {
    const cargarDatosIniciales = async () => {
      setIsLoading(true);
      setError('');
      try {
        const [listaCursos, resumenDiario] = await Promise.all([
          obtenerCursos(),
          obtenerResumenDiario(),
        ]);
        setCursos(listaCursos);
        setResumen(resumenDiario);
      } catch {
        setError('Error al cargar los datos. Intenta nuevamente.');
      } finally {
        setIsLoading(false);
      }
    };

    cargarDatosIniciales();
  }, []);

  const handleFiltrar = async ({ curso, fecha }) => {
    setIsLoading(true);
    setError('');
    try {
      const [listaEstudiantes, resumenDiario] = await Promise.all([
        obtenerAsistenciaPorCurso(curso, fecha),
        obtenerResumenDiario(curso, fecha),
      ]);
      const cursoNombre = resumenDiario?.nombreCurso ?? cursos.find(c => c.id == curso)?.nombre ?? String(curso);
      let listado = listaEstudiantes;
      if (!listado || listado.length === 0) {
        const matricula = await obtenerAlumnosPorCurso(curso);
        listado = matricula.map(m => ({
          id: m.id,
          estudianteId: m.id,
          nombre: `${m.nombre} ${m.apellido}`.trim() || m.id,
          curso: cursoNombre,
          estado: 'presente',
          hora: null,
        }));
      } else {
        // Deduplicar por estudianteId: si el mismo estudiante tiene múltiples
        // registros para esa fecha (sesiones anteriores), quedarse con el último.
        const mapa = new Map();
        listado.forEach(item => {
          const key = item.estudianteId ?? item.id;
          mapa.set(key, { ...item, curso: cursoNombre });
        });
        listado = Array.from(mapa.values());
      }
      setEstudiantes(listado);
      setResumen(resumenDiario);
      setFiltroActual({ curso, fecha });
    } catch {
      setError('Error al aplicar los filtros. Intenta nuevamente.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleCambiarEstado = (id, nuevoEstado) => {
    setEstudiantes((prev) =>
      prev.map((e) => (e.id === id ? { ...e, estado: nuevoEstado } : e))
    );
  };

  const handleGuardar = async () => {
    if (!filtroActual) return;
    setError('');
    try {
      await guardarAsistencia(filtroActual.curso, filtroActual.fecha, estudiantes);
      showToast('Asistencia guardada correctamente.');
    } catch {
      showToast('Error al guardar la asistencia.', 'error');
    }
  };

  return (
    <main className="asistencia-page">
      <header className="asistencia-page__header">
        <div className="asistencia-page__title-container">
          <div className="asistencia-page__icon">
            <ClipboardCheck size={24} />
          </div>
          <div>
            <h1 className="asistencia-page__title">Gestión de Asistencia</h1>
            <p className="asistencia-page__subtitle">Control diario de asistencia por curso</p>
          </div>
        </div>
      </header>

      <section className="asistencia-page__content">
        {error && <p className="asistencia-page__subtitle">{error}</p>}

        <ResumenAsistencia resumen={resumen} />
        <FiltroAsistencia cursos={cursos} onFiltrar={handleFiltrar} />

        {isLoading
          ? <p className="asistencia-page__subtitle">Cargando...</p>
          : (
            <>
              <AsistenciaTable estudiantes={estudiantes} onCambiarEstado={handleCambiarEstado} />
              {estudiantes.length > 0 && (
                <div className="asistencia-page__actions">
                  <button className="asistencia-page__btn-guardar" onClick={handleGuardar}>
                    Guardar Asistencia
                  </button>
                </div>
              )}
            </>
          )
        }
      </section>

      <Toast toast={toast} onClose={() => {}} />
    </main>
  );
}

export default AsistenciaPage;
