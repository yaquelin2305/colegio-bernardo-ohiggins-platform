import { useState, useEffect } from 'react';
import { Users } from 'lucide-react';
import MainLayout from '../../../shared/components/layout/MainLayout';
import { useAuth } from '../../../core/context/AuthContext';
import EncabezadoBoletin from '../components/EncabezadoBoletin';
import TablaBoletinNotas from '../components/TablaBoletinNotas';
import {
  obtenerBoletinPropio,
  obtenerPupilos,
  obtenerBoletinPupilo,
} from '../services/gestionAcademicaService';
import '../styles/VisualizadorNotasPage.css';

function VisualizadorNotasPage() {
  const { usuario } = useAuth();
  const esApoderado = usuario?.rol === 'APODERADO';

  const [boletin, setBoletin] = useState(null);
  const [pupilos, setPupilos] = useState([]);
  const [pupilId, setPupilId] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (esApoderado) {
      obtenerPupilos()
        .then(data => {
          setPupilos(data);
          if (data.length > 0) setPupilId(data[0].id);
        })
        .catch(() => setError('No se pudo cargar el listado de pupilos.'))
        .finally(() => setIsLoading(false));
    } else {
      obtenerBoletinPropio()
        .then(setBoletin)
        .catch(() => setError('No se pudo cargar el boletín de notas.'))
        .finally(() => setIsLoading(false));
    }
  }, [esApoderado]);

  useEffect(() => {
    if (!esApoderado || !pupilId) return;
    setIsLoading(true);
    obtenerBoletinPupilo(pupilId)
      .then(setBoletin)
      .catch(() => setError('No se pudo cargar el boletín del pupilo.'))
      .finally(() => setIsLoading(false));
  }, [esApoderado, pupilId]);

  const alumno      = boletin?.alumno      ?? null;
  const asignaturas = boletin?.asignaturas ?? [];

  return (
    <MainLayout titulo="Mi Boletín de Notas">
      <div className="visualizador-notas">

        {esApoderado && pupilos.length > 0 && (
          <div className="boletin__selector-pupilo">
            <label htmlFor="select-pupilo" className="boletin__selector-label">
              <Users size={15} aria-hidden="true" />
              Seleccionar Pupilo
            </label>
            <select
              id="select-pupilo"
              className="boletin__selector-select"
              value={pupilId}
              onChange={e => setPupilId(e.target.value)}
            >
              {pupilos.map(p => (
                <option key={p.id} value={p.id}>
                  {p.nombre} — {p.curso}
                </option>
              ))}
            </select>
          </div>
        )}

        {isLoading && <p className="boletin__cargando">Cargando...</p>}
        {error && <p className="boletin__error">{error}</p>}

        {!isLoading && !error && alumno && <EncabezadoBoletin alumno={alumno} />}

        {!isLoading && !error && <TablaBoletinNotas asignaturas={asignaturas} />}

      </div>
    </MainLayout>
  );
}

export default VisualizadorNotasPage;
