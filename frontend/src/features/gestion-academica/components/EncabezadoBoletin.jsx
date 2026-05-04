import { TrendingUp, CalendarCheck, UserCircle } from 'lucide-react';

function EncabezadoBoletin({ alumno }) {
  return (
    <section className="boletin__encabezado" aria-label="Datos del alumno">
      <div className="boletin__alumno-info">
        <div className="boletin__avatar">
          <UserCircle size={48} aria-hidden="true" />
        </div>
        <div>
          <h2 className="boletin__nombre">{alumno.nombre}</h2>
          <p className="boletin__meta">
            <span>{alumno.rut}</span>
            <span className="boletin__separador" aria-hidden="true">·</span>
            <span>{alumno.curso}</span>
            <span className="boletin__separador" aria-hidden="true">·</span>
            <span>{alumno.periodo}</span>
          </p>
        </div>
      </div>

      <div className="boletin__stats">
        <article className="boletin__stat-card boletin__stat-card--accent" aria-label="Promedio general">
          <div className="boletin__stat-icono boletin__stat-icono--accent">
            <TrendingUp size={20} aria-hidden="true" />
          </div>
          <span className="boletin__stat-label">Promedio General</span>
          <span className="boletin__stat-valor">{alumno.promedioGeneral.toFixed(1)}</span>
          <span className="boletin__stat-detalle">Sobre 7.0 máximo</span>
        </article>

        <article className="boletin__stat-card boletin__stat-card--primary" aria-label="Porcentaje de asistencia">
          <div className="boletin__stat-icono boletin__stat-icono--primary">
            <CalendarCheck size={20} aria-hidden="true" />
          </div>
          <span className="boletin__stat-label">% Asistencia Total</span>
          <span className="boletin__stat-valor">{alumno.asistencia}%</span>
          <span className="boletin__stat-detalle">Mínimo requerido: 85%</span>
        </article>
      </div>
    </section>
  );
}

export default EncabezadoBoletin;
