import { useState, useEffect } from 'react';
import { useNavigate, useOutletContext } from 'react-router-dom';
import CabeceraBandeja from '../components/CabeceraBandeja';
import ListaMensajes from '../components/ListaMensajes';
import { obtenerMensajes } from '../services/comunicacionesService';
import '../styles/BandejaMensajesPage.css';

function BandejaMensajesPage() {
  const { setTitulo } = useOutletContext();
  const navigate = useNavigate();

  useEffect(() => { setTitulo('Comunicaciones'); }, [setTitulo]);
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
  );
}

export default BandejaMensajesPage;
