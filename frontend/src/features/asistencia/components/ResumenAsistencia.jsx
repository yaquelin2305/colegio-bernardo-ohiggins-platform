import { Users, CheckCircle, XCircle, Clock3 } from 'lucide-react';
import '../styles/ResumenAsistencia.css';

const RESUMEN_VACIO = { total: 0, presentes: 0, ausentes: 0, porcentaje: 0 };

function ResumenAsistencia({ resumen = RESUMEN_VACIO }) {
  const datos = resumen ?? RESUMEN_VACIO;

  const cards = [
    {
      icon: <Users size={24} />,
      iconClass: 'resumen-asistencia__icon--total',
      label: 'Total Estudiantes',
      value: datos.total,
      valueClass: '',
    },
    {
      icon: <CheckCircle size={24} />,
      iconClass: 'resumen-asistencia__icon--presente',
      label: 'Presentes Hoy',
      value: datos.presentes,
      valueClass: 'resumen-asistencia__value--presente',
    },
    {
      icon: <XCircle size={24} />,
      iconClass: 'resumen-asistencia__icon--ausente',
      label: 'Ausentes Hoy',
      value: datos.ausentes,
      valueClass: 'resumen-asistencia__value--ausente',
    },
    {
      icon: <Clock3 size={24} />,
      iconClass: 'resumen-asistencia__icon--porcentaje',
      label: '% Asistencia',
      value: `${datos.porcentaje}%`,
      valueClass: 'resumen-asistencia__value--presente',
    },
  ];

  return (
    <section className="resumen-asistencia" aria-label="Resumen de asistencia">
      {cards.map((card, index) => (
        <article key={index} className="resumen-asistencia__card">
          <div className={`resumen-asistencia__icon ${card.iconClass}`}>{card.icon}</div>
          <div className="resumen-asistencia__info">
            <span className="resumen-asistencia__label">{card.label}</span>
            <span className={`resumen-asistencia__value ${card.valueClass}`}>{card.value}</span>
          </div>
        </article>
      ))}
    </section>
  );
}

export default ResumenAsistencia;
