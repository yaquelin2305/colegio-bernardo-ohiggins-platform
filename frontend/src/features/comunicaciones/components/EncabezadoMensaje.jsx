import { Mail, Calendar, User, Tag } from 'lucide-react';

function EncabezadoMensaje({ mensaje }) {
  return (
    <section className="detalle-mensaje__encabezado" aria-label="Información del mensaje">
      <div className="detalle-mensaje__asunto-wrapper">
        <Mail size={20} aria-hidden="true" className="detalle-mensaje__icono-asunto" />
        <h2 className="detalle-mensaje__asunto">{mensaje.asunto}</h2>
        {!mensaje.leido && (
          <span className="detalle-mensaje__badge-nuevo">Nuevo</span>
        )}
      </div>

      <dl className="detalle-mensaje__meta">
        <div className="detalle-mensaje__meta-item">
          <dt className="detalle-mensaje__meta-etiqueta">
            <User size={13} aria-hidden="true" />
            Remitente
          </dt>
          <dd className="detalle-mensaje__meta-valor">{mensaje.remitente}</dd>
        </div>
        <div className="detalle-mensaje__meta-item">
          <dt className="detalle-mensaje__meta-etiqueta">
            <Calendar size={13} aria-hidden="true" />
            Fecha
          </dt>
          <dd className="detalle-mensaje__meta-valor">{mensaje.fecha}</dd>
        </div>
        <div className="detalle-mensaje__meta-item">
          <dt className="detalle-mensaje__meta-etiqueta">
            <Tag size={13} aria-hidden="true" />
            Tipo
          </dt>
          <dd className="detalle-mensaje__meta-valor">
            <span className={`detalle-mensaje__etiqueta detalle-mensaje__etiqueta--${mensaje.tipo.toLowerCase().replace(' ', '-')}`}>
              {mensaje.tipo}
            </span>
          </dd>
        </div>
        <div className="detalle-mensaje__meta-item">
          <dt className="detalle-mensaje__meta-etiqueta">Canal</dt>
          <dd className="detalle-mensaje__meta-valor">{mensaje.canal}</dd>
        </div>
      </dl>
    </section>
  );
}

export default EncabezadoMensaje;
