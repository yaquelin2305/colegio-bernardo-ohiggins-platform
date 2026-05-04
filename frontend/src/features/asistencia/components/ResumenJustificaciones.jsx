import { AlertTriangle, FileCheck } from 'lucide-react';

function ResumenJustificaciones({ totalPendientes, totalJustificadas }) {
  return (
    <div className="justificacion__resumen">
      <div className="justificacion__stat">
        <div className="justificacion__stat-icono-wrapper justificacion__stat-icono-wrapper--alerta">
          <AlertTriangle size={18} aria-hidden="true" />
        </div>
        <div className="justificacion__stat-texto">
          <span className="justificacion__stat-numero">{totalPendientes}</span>
          <span className="justificacion__stat-etiqueta">Pendientes</span>
        </div>
      </div>
      <div className="justificacion__stat">
        <div className="justificacion__stat-icono-wrapper justificacion__stat-icono-wrapper--ok">
          <FileCheck size={18} aria-hidden="true" />
        </div>
        <div className="justificacion__stat-texto">
          <span className="justificacion__stat-numero">{totalJustificadas}</span>
          <span className="justificacion__stat-etiqueta">Justificadas</span>
        </div>
      </div>
    </div>
  );
}

export default ResumenJustificaciones;
