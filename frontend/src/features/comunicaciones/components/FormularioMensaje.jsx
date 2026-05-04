import { Send } from 'lucide-react';

const canales = [
  { value: 'plataforma-correo', label: 'Plataforma y Correo',  desc: 'Notifica en el sistema y envía copia por email' },
  { value: 'solo-plataforma',   label: 'Solo Plataforma',      desc: 'Visible únicamente dentro del sistema' },
  { value: 'sms-urgencia',      label: 'SMS de Urgencia',      desc: 'Envío inmediato vía mensaje de texto al apoderado' },
];

function FormularioMensaje({ formulario, onChange, onEnviar, isLoading = false }) {
  return (
    <form className="redactar__tarjeta" onSubmit={onEnviar} noValidate>

      <div className="redactar__campo">
        <label htmlFor="destinatario" className="redactar__label">Destinatario</label>
        <select
          id="destinatario"
          name="destinatario"
          className="redactar__select"
          value={formulario.destinatario}
          onChange={onChange}
          required
        >
          <option value="" disabled>Seleccionar destinatario...</option>
          <option value="apoderados-1a">Apoderados 1° Medio A</option>
          <option value="apoderados-2a">Apoderados 2° Medio A</option>
          <option value="alumnos-1a">Alumnos 1° Medio A</option>
          <option value="centro-alumnos">Centro de Alumnos</option>
          <option value="docentes">Cuerpo Docente</option>
          <option value="direccion">Dirección</option>
          <option value="utp">UTP</option>
        </select>
      </div>

      <div className="redactar__campo">
        <label htmlFor="asunto" className="redactar__label">Asunto</label>
        <input
          id="asunto"
          name="asunto"
          type="text"
          className="redactar__input"
          placeholder="Escribe el asunto del mensaje..."
          value={formulario.asunto}
          onChange={onChange}
          required
        />
      </div>

      <div className="redactar__campo">
        <label htmlFor="mensaje" className="redactar__label">Mensaje</label>
        <textarea
          id="mensaje"
          name="mensaje"
          className="redactar__textarea"
          rows={7}
          placeholder="Redacta tu mensaje aquí..."
          value={formulario.mensaje}
          onChange={onChange}
          required
        />
      </div>

      <div className="redactar__campo">
        <span className="redactar__label" id="canal-label">Canal de Envío</span>
        <div className="redactar__canales" role="radiogroup" aria-labelledby="canal-label">
          {canales.map(canal => (
            <label
              key={canal.value}
              className={`redactar__canal-opcion ${formulario.canal === canal.value ? 'redactar__canal-opcion--activo' : ''}`}
            >
              <input
                type="radio"
                name="canal"
                value={canal.value}
                checked={formulario.canal === canal.value}
                onChange={onChange}
                className="redactar__radio-hidden"
                aria-describedby={`desc-${canal.value}`}
              />
              <span className="redactar__canal-nombre">{canal.label}</span>
              <span id={`desc-${canal.value}`} className="redactar__canal-desc">{canal.desc}</span>
            </label>
          ))}
        </div>
      </div>

      <div className="redactar__acciones">
        <button type="submit" className="redactar__btn-enviar" disabled={isLoading}>
          <Send size={16} aria-hidden="true" />
          {isLoading ? 'Enviando...' : 'Enviar Mensaje'}
        </button>
      </div>

    </form>
  );
}

export default FormularioMensaje;
