import { useState, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import ResumenJustificaciones from '../components/ResumenJustificaciones';
import TablaInasistenciasPendientes from '../components/TablaInasistenciasPendientes';
import TablaInasistenciasJustificadas from '../components/TablaInasistenciasJustificadas';
import { obtenerInasistencias, justificarInasistencia } from '../services/asistenciaService';
import '../styles/JustificacionInasistenciasPage.css';

const formularioInicial = { motivo: '', archivo: null };

function JustificacionInasistenciasPage() {
  const { setTitulo } = useOutletContext();
  const [inasistencias, setInasistencias] = useState([]);

  useEffect(() => { setTitulo('Justificación de Inasistencias'); }, [setTitulo]);
  const [formularioActivo, setFormularioActivo] = useState(null);
  const [formulario, setFormulario] = useState(formularioInicial);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    obtenerInasistencias()
      .then(setInasistencias)
      .catch(() => setError('No se pudo cargar las inasistencias.'))
      .finally(() => setIsLoading(false));
  }, []);

  function handleAbrir(id) {
    setFormularioActivo(id);
    setFormulario(formularioInicial);
  }

  function handleCerrar() {
    setFormularioActivo(null);
    setFormulario(formularioInicial);
  }

  function handleChange(e) {
    const { name, value, files } = e.target;
    setFormulario(prev => ({ ...prev, [name]: files ? files[0] : value }));
  }

  async function handleJustificar(e) {
    e.preventDefault();
    if (!formulario.motivo.trim()) return;
    await justificarInasistencia(formularioActivo, formulario);
    setInasistencias(prev =>
      prev.map(i => i.id === formularioActivo ? { ...i, justificada: true } : i)
    );
    handleCerrar();
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

    </div>
  );
}

export default JustificacionInasistenciasPage;
