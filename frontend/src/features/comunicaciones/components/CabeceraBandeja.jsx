import { PenLine } from 'lucide-react';

function CabeceraBandeja({ totalNoLeidos, onRedactar }) {
  return (
    <div className="bandeja__cabecera">
      <p className="bandeja__subtitulo">
        {totalNoLeidos} mensajes sin leer
      </p>
      <button
        className="bandeja__btn-redactar"
        onClick={onRedactar}
        aria-label="Redactar nuevo mensaje"
      >
        <PenLine size={16} aria-hidden="true" />
        Redactar Nuevo
      </button>
    </div>
  );
}

export default CabeceraBandeja;
