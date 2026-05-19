import { useState, useEffect } from 'react';
import { useOutletContext } from 'react-router-dom';
import { useAuth } from '../../../core/context/useAuth';
import EncabezadoBoletin from '../components/EncabezadoBoletin';
import TablaBoletinNotas from '../components/TablaBoletinNotas';
import {
  obtenerBoletinPropio,
  obtenerBoletinPupilo,
  getPupiloUuidFromToken,
} from '../services/gestionAcademicaService';
import '../styles/VisualizadorNotasPage.css';

function VisualizadorNotasPage() {
  const { setTitulo } = useOutletContext();
  const { usuario } = useAuth();

  useEffect(() => { setTitulo('Mi Boletín de Notas'); }, [setTitulo]);
  const esApoderado = usuario?.rol === 'APODERADO';

  const [boletin, setBoletin] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (esApoderado) {
      const pupiloUuid = getPupiloUuidFromToken();
      if (!pupiloUuid) {
        setError('No tienes un pupilo asociado a tu cuenta.');
        setIsLoading(false);
        return;
      }
      obtenerBoletinPupilo(pupiloUuid)
        .then(setBoletin)
        .catch(() => setError('No se pudo cargar el boletín del pupilo.'))
        .finally(() => setIsLoading(false));
    } else {
      obtenerBoletinPropio()
        .then(setBoletin)
        .catch(() => setError('No se pudo cargar el boletín de notas.'))
        .finally(() => setIsLoading(false));
    }
  }, [esApoderado]);

  const alumno      = boletin?.alumno      ?? null;
  const asignaturas = boletin?.asignaturas ?? [];

  return (
    <div className="visualizador-notas">
      {isLoading && <p className="boletin__cargando">Cargando...</p>}
      {error && <p className="boletin__error">{error}</p>}

      {!isLoading && !error && alumno && <EncabezadoBoletin alumno={alumno} />}
      {!isLoading && !error && <TablaBoletinNotas asignaturas={asignaturas} />}
    </div>
  );
}

export default VisualizadorNotasPage;
