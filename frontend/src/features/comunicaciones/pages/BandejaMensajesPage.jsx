import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import MainLayout from '../../../shared/components/layout/MainLayout';
import CabeceraBandeja from '../components/CabeceraBandeja';
import ListaMensajes from '../components/ListaMensajes';
import { obtenerMensajes } from '../services/comunicacionesService';
import '../styles/BandejaMensajesPage.css';

function BandejaMensajesPage() {
  const navigate = useNavigate();
  const [mensajes, setMensajes] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    obtenerMensajes()
      .then(setMensajes)
      .catch(() => setError('No se pudo cargar la bandeja de mensajes.'))
      .finally(() => setIsLoading(false));
  }, []);

  return (
    <MainLayout titulo="Comunicaciones">
      <div className="bandeja">
        <CabeceraBandeja
          totalNoLeidos={mensajes.filter(m => !m.leido).length}
          onRedactar={() => navigate('/comunicaciones/redactar')}
        />

        {isLoading && <p className="bandeja__cargando">Cargando...</p>}
        {error && <p className="bandeja__error">{error}</p>}

        {!isLoading && !error && (
          <ListaMensajes
            mensajes={mensajes}
            onSeleccionarMensaje={id => navigate(`/comunicaciones/${id}`)}
          />
        )}
      </div>
    </MainLayout>
  );
}

export default BandejaMensajesPage;
