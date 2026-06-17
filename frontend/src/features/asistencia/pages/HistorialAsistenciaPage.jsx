import { useState, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import FiltroAlumno from '../components/FiltroAlumno';
import InfoAlumno from '../components/InfoAlumno';
import TablaHistorialAsistencia from '../components/TablaHistorialAsistencia';
import { obtenerAlumnos, obtenerAlumnosPorDocente, obtenerHistorialAsistencia } from '../services/asistenciaService';
import axiosClient from '../../../core/api/axiosClient';
import { getPupiloUuidFromToken } from '../../../shared/utils/tokenUtils';
import { useAuth } from '../../../core/context/useAuth';
import '../styles/HistorialAsistenciaPage.css';

function HistorialAsistenciaPage() {
  const { setTitulo } = useOutletContext();
  const { usuario } = useAuth();
  const [alumnos, setAlumnos] = useState([]);

  useEffect(() => { setTitulo('Historial de Asistencia'); }, [setTitulo]);
  const [alumnoId, setAlumnoId] = useState('');
  const [registros, setRegistros] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const rol = usuario?.rol;
  const personal = rol === 'ESTUDIANTE' || rol === 'APODERADO';

  useEffect(() => {
    if (!rol) return;
    if (personal) {
      const uuid = rol === 'APODERADO' ? getPupiloUuidFromToken() : (usuario?.userId ?? usuario?.sub);
      if (!uuid) { setError('No se pudo identificar al alumno.'); setIsLoading(false); return; }
      axiosClient.get(`/bff/boletin/${uuid}`)
        .then(({ data }) => {
          setAlumnos([{
            id: uuid,
            nombre: data.nombreCompleto ?? '',
            rut: data.rut ?? '',
            curso: data.curso ?? '',
          }]);
          setAlumnoId(uuid);
        })
        .catch(() => setError('No se pudo cargar el alumno.'))
        .finally(() => setIsLoading(false));
    } else if (rol === 'DOCENTE') {
      obtenerAlumnosPorDocente()
        .then(setAlumnos)
        .catch(() => setError('No se pudo cargar el listado de alumnos.'))
        .finally(() => setIsLoading(false));
    } else {
      obtenerAlumnos()
        .then(setAlumnos)
        .catch(() => setError('No se pudo cargar el listado de alumnos.'))
        .finally(() => setIsLoading(false));
    }
  }, [personal, rol, usuario?.userId, usuario?.sub]);

  useEffect(() => {
    if (!alumnoId) return;
    const cargar = async () => {
      try {
        const registros = await obtenerHistorialAsistencia(alumnoId);
        setRegistros(registros);
        if (!personal && rol !== 'DOCENTE') {
          const { data } = await axiosClient.get(`/bff/boletin/${alumnoId}`).catch(() => ({ data: null }));
          if (data?.curso) {
            setAlumnos(prev => prev.map(a => a.id === alumnoId ? { ...a, curso: data.curso } : a));
          }
        }
      } catch {
        setError('No se pudo cargar el historial de asistencia.');
      }
    };
    cargar();
  }, [alumnoId, personal, rol]);

  const alumnoSeleccionado = alumnos.find(a => a.id === alumnoId) || null;
  const totalPresentes = registros.filter(r => r.estado === 'presente').length;
  const porcentaje = registros.length > 0
    ? Math.round((totalPresentes / registros.length) * 100)
    : null;

  return (
    <div className="historial">

      {isLoading && <p className="historial__cargando">Cargando...</p>}
      {error && <p className="historial__error">{error}</p>}

      {!isLoading && !error && !personal && (
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

      {!isLoading && !error && !alumnoId && !personal && (
        <p className="historial__instruccion">Selecciona un alumno para ver su historial de asistencia.</p>
      )}

    </div>
  );
}

export default HistorialAsistenciaPage;
