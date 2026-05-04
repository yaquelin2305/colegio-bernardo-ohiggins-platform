import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft } from 'lucide-react';
import MainLayout from '../../../shared/components/layout/MainLayout';
import FormularioMensaje from '../components/FormularioMensaje';
import { enviarMensaje } from '../services/comunicacionesService';
import '../styles/RedactarMensajePage.css';

const estadoInicial = {
  destinatario: '',
  asunto: '',
  mensaje: '',
  canal: 'plataforma-correo',
};

function RedactarMensajePage() {
  const [formulario, setFormulario] = useState(estadoInicial);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  function handleChange(e) {
    const { name, value } = e.target;
    setFormulario(prev => ({ ...prev, [name]: value }));
  }

  async function handleEnviar(e) {
    e.preventDefault();
    setIsLoading(true);
    setError(null);

    try {
      await enviarMensaje({ ...formulario, fecha: new Date().toISOString() });
      navigate('/comunicaciones');
    } catch {
      setError('No se pudo enviar el mensaje. Intenta nuevamente.');
    } finally {
      setIsLoading(false);
    }
  }

  return (
    <MainLayout titulo="Redactar Mensaje">
      <div className="redactar">
        <button
          className="redactar__volver"
          onClick={() => navigate('/comunicaciones')}
          aria-label="Volver a la bandeja"
        >
          <ArrowLeft size={16} aria-hidden="true" />
          Volver a Comunicaciones
        </button>

        {error && <p className="redactar__error">{error}</p>}

        <FormularioMensaje
          formulario={formulario}
          onChange={handleChange}
          onEnviar={handleEnviar}
          isLoading={isLoading}
        />
      </div>
    </MainLayout>
  );
}

export default RedactarMensajePage;
