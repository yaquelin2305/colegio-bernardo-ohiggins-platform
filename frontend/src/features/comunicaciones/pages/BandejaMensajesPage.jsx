import { useNavigate } from 'react-router-dom';
import { Mail, AlertCircle, PenLine } from 'lucide-react';
import MainLayout from '../../../shared/components/layout/MainLayout';
import '../styles/BandejaMensajesPage.css';

const mensajes = [];

function BandejaMensajesPage() {
  const navigate = useNavigate();

  return (
    <MainLayout titulo="Comunicaciones">
      <div className="bandeja">

        <div className="bandeja__cabecera">
          <p className="bandeja__subtitulo">
            {mensajes.filter(m => !m.leido).length} mensajes sin leer
          </p>
          <button
            className="bandeja__btn-redactar"
            onClick={() => navigate('/comunicaciones/redactar')}
            aria-label="Redactar nuevo mensaje"
          >
            <PenLine size={16} aria-hidden="true" />
            Redactar Nuevo
          </button>
        </div>

        <ul className="bandeja__lista" role="list">
          {mensajes.map(mensaje => (
            <li
              key={mensaje.id}
              className={`bandeja__tarjeta ${!mensaje.leido ? 'bandeja__tarjeta--no-leido' : 'bandeja__tarjeta--leido'}`}
              role="listitem"
            >
              <div className="bandeja__icono-wrapper">
                {!mensaje.leido
                  ? <AlertCircle size={20} className="bandeja__icono bandeja__icono--alerta" aria-hidden="true" />
                  : <Mail size={20} className="bandeja__icono bandeja__icono--leido" aria-hidden="true" />
                }
              </div>

              <div className="bandeja__contenido">
                <div className="bandeja__fila-superior">
                  <span className="bandeja__remitente">{mensaje.remitente}</span>
                  <span className={`bandeja__etiqueta bandeja__etiqueta--${mensaje.tipo.toLowerCase().replace(' ', '-')}`}>
                    {mensaje.tipo}
                  </span>
                </div>
                <p className="bandeja__asunto">{mensaje.asunto}</p>
              </div>

              <span className="bandeja__fecha">{mensaje.fecha}</span>
            </li>
          ))}
        </ul>

      </div>
    </MainLayout>
  );
}

export default BandejaMensajesPage;
