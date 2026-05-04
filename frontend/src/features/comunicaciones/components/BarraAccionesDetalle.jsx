import { ArrowLeft, Reply } from 'lucide-react';

function BarraAccionesDetalle({ onVolver, onResponder }) {
  return (
    <div className="detalle-mensaje__barra">
      <button
        className="detalle-mensaje__btn-volver"
        onClick={onVolver}
        aria-label="Volver a la bandeja de mensajes"
      >
        <ArrowLeft size={16} aria-hidden="true" />
        Volver
      </button>
      <button
        className="detalle-mensaje__btn-responder"
        onClick={onResponder}
        aria-label="Responder este mensaje"
      >
        <Reply size={16} aria-hidden="true" />
        Responder
      </button>
    </div>
  );
}

export default BarraAccionesDetalle;
