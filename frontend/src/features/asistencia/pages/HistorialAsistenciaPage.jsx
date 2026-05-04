import { useState, useEffect } from 'react';
import MainLayout from '../../../shared/components/layout/MainLayout';
import FiltroAlumno from '../components/FiltroAlumno';
import InfoAlumno from '../components/InfoAlumno';
import TablaHistorialAsistencia from '../components/TablaHistorialAsistencia';
import { obtenerAlumnos, obtenerHistorialAsistencia } from '../services/asistenciaService';
import '../styles/HistorialAsistenciaPage.css';

function HistorialAsistenciaPage() {
  const [alumnos, setAlumnos] = useState([]);
  const [alumnoId, setAlumnoId] = useState('');
  const [registros, setRegistros] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    obtenerAlumnos()
      .then(setAlumnos)
      .catch(() => setError('No se pudo cargar el listado de alumnos.'))
      .finally(() => setIsLoading(false));
  }, []);

  useEffect(() => {
    if (!alumnoId) {
      setRegistros([]);
      return;
    }
    obtenerHistorialAsistencia(alumnoId)
      .then(setRegistros)
      .catch(() => setError('No se pudo cargar el historial de asistencia.'));
  }, [alumnoId]);

  const alumnoSeleccionado = alumnos.find(a => a.id === alumnoId) || null;
  const totalPresentes = registros.filter(r => r.estado === 'presente').length;
  const porcentaje = registros.length > 0
    ? Math.round((totalPresentes / registros.length) * 100)
    : null;

  return (
    <MainLayout titulo="Historial de Asistencia">
      <div className="historial">

        {isLoading && <p className="historial__cargando">Cargando...</p>}
        {error && <p className="historial__error">{error}</p>}

        {!isLoading && !error && (
          <FiltroAlumno
            alumnos={alumnos}
            alumnoId={alumnoId}
            onChange={e => setAlumnoId(e.target.value)}
          />
        )}

        {alumnoSeleccionado && (
          <InfoAlumno alumno={alumnoSeleccionado} porcentaje={porcentaje} />
        )}

        {alumnoId && <TablaHistorialAsistencia registros={registros} />}

        {!isLoading && !error && !alumnoId && (
          <p className="historial__instruccion">Selecciona un alumno para ver su historial de asistencia.</p>
        )}

      </div>
    </MainLayout>
  );
}

export default HistorialAsistenciaPage;
