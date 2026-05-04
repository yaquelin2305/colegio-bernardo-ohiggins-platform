import { Mail, AlertCircle } from 'lucide-react';

function ListaMensajes({ mensajes, onSeleccionarMensaje }) {
  if (mensajes.length === 0) {
    return <p className="bandeja__vacio">No hay mensajes en tu bandeja.</p>;
  }

  return (
    <ul className="bandeja__lista" role="list">
      {mensajes.map(mensaje => (
        <li
          key={mensaje.id}
          className={`bandeja__tarjeta ${!mensaje.leido ? 'bandeja__tarjeta--no-leido' : 'bandeja__tarjeta--leido'}`}
          role="listitem"
          onClick={() => onSeleccionarMensaje(mensaje.id)}
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
  );
}

export default ListaMensajes;
