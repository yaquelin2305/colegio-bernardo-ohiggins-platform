import { useState, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import ResumenJustificaciones from '../components/ResumenJustificaciones';
import TablaInasistenciasPendientes from '../components/TablaInasistenciasPendientes';
import TablaInasistenciasJustificadas from '../components/TablaInasistenciasJustificadas';
import { obtenerInasistencias, obtenerHistorialAsistencia, justificarInasistencia } from '../services/asistenciaService';
import { getPupiloUuidFromToken } from '../../../shared/utils/tokenUtils';
import { useAuth } from '../../../core/context/useAuth';
import { useToast } from '../../../shared/hooks/useToast';
import Toast from '../../../shared/components/ui/Toast';
import '../styles/JustificacionInasistenciasPage.css';

const formularioInicial = { motivo: '' };

function JustificacionInasistenciasPage() {
  const { setTitulo } = useOutletContext();
  const { usuario } = useAuth();
  const [inasistencias, setInasistencias] = useState([]);

  useEffect(() => { setTitulo('Justificación de Inasistencias'); }, [setTitulo]);
  const [formularioActivo, setFormularioActivo] = useState(null);
  const [formulario, setFormulario] = useState(formularioInicial);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const { toast, showToast } = useToast();

  useEffect(() => {
    const esApoderado = usuario?.rol === 'APODERADO';
    const cargar = esApoderado
      ? (async () => {
          const pupiloUuid = getPupiloUuidFromToken();
          if (!pupiloUuid) return [];
          const historial = await obtenerHistorialAsistencia(pupiloUuid);
          return historial
            .filter(h => {
              const estado = (h.estado ?? '').toLowerCase();
              return estado === 'ausente' || estado === 'justificado';
            })
            .map(h => ({
              id: h.id,
              fecha: h.fecha,
              alumno: h.nombre ?? 'Pupilo',
              curso: '',
              justificada: (h.estado ?? '').toLowerCase() === 'justificado',
            }));
        })()
      : obtenerInasistencias();

    Promise.resolve(cargar)
      .then(setInasistencias)
      .catch(() => setError('No se pudo cargar las inasistencias.'))
      .finally(() => setIsLoading(false));
  }, [usuario?.rol]);

  function handleAbrir(id) {
    setFormularioActivo(id);
    setFormulario(formularioInicial);
  }

  function handleCerrar() {
    setFormularioActivo(null);
    setFormulario(formularioInicial);
  }

  function handleChange(e) {
    const { name, value } = e.target;
    setFormulario(prev => ({ ...prev, [name]: value }));
  }

  async function handleJustificar(e) {
    e.preventDefault();
    if (!formulario.motivo.trim()) return;
    try {
      await justificarInasistencia(formularioActivo, formulario);
      setInasistencias(prev =>
        prev.map(i => i.id === formularioActivo ? { ...i, justificada: true } : i)
      );
      handleCerrar();
      showToast('Inasistencia justificada correctamente.');
    } catch {
      showToast('No se pudo justificar la inasistencia.', 'error');
    }
  }

  const pendientes   = inasistencias.filter(i => !i.justificada);
  const justificadas = inasistencias.filter(i => i.justificada);

  return (
    <div className="justificacion">

      {isLoading && <p className="justificacion__cargando">Cargando...</p>}
      {error && <p className="justificacion__error">{error}</p>}

      {!isLoading && !error && (
        <>
          <ResumenJustificaciones
            totalPendientes={pendientes.length}
            totalJustificadas={justificadas.length}
          />

          <TablaInasistenciasPendientes
            pendientes={pendientes}
            formularioActivo={formularioActivo}
            formulario={formulario}
            onAbrir={handleAbrir}
            onCerrar={handleCerrar}
            onChange={handleChange}
            onJustificar={handleJustificar}
          />

          <TablaInasistenciasJustificadas justificadas={justificadas} />
        </>
      )}

      <Toast toast={toast} onClose={() => {}} />
    </div>
  );
}

export default JustificacionInasistenciasPage;
