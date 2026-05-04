import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import MainLayout from '../../../shared/components/layout/MainLayout';
import BarraAccionesDetalle from '../components/BarraAccionesDetalle';
import EncabezadoMensaje from '../components/EncabezadoMensaje';
import CuerpoMensaje from '../components/CuerpoMensaje';
import { obtenerMensajePorId } from '../services/comunicacionesService';
import '../styles/DetalleMensajePage.css';

function DetalleMensajePage() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [mensaje, setMensaje] = useState(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    obtenerMensajePorId(id)
      .then(setMensaje)
      .catch(() => setError('No se pudo cargar el mensaje.'))
      .finally(() => setIsLoading(false));
  }, [id]);

  if (isLoading) {
    return (
      <MainLayout titulo="Detalle de Mensaje">
        <p className="detalle-mensaje__cargando">Cargando...</p>
      </MainLayout>
    );
  }

  if (error || !mensaje) {
    return (
      <MainLayout titulo="Mensaje no encontrado">
        <div className="detalle-mensaje__no-encontrado">
          <p>{error ?? 'El mensaje solicitado no existe o no está disponible.'}</p>
          <button className="detalle-mensaje__btn-volver" onClick={() => navigate('/comunicaciones')}>
            <ArrowLeft size={16} aria-hidden="true" />
            Volver a la bandeja
          </button>
        </div>
      </MainLayout>
    );
  }

  return (
    <MainLayout titulo="Detalle de Mensaje">
      <div className="detalle-mensaje">
        <BarraAccionesDetalle
          onVolver={() => navigate('/comunicaciones')}
          onResponder={() => navigate('/comunicaciones/redactar')}
        />
        <EncabezadoMensaje mensaje={mensaje} />
        <CuerpoMensaje cuerpo={mensaje.cuerpo} />
      </div>
    </MainLayout>
  );
}

export default DetalleMensajePage;
