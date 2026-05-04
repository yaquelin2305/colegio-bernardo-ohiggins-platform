import { Users, Calendar, CheckCircle, XCircle, Clock3 } from 'lucide-react';
import '../styles/ResumenAsistencia.css';

const MOCK_DATA = {
  total: 32,
  presentes: 28,
  ausentes: 4,
  porcentaje: 87.5,
};

function ResumenAsistencia() {
  const cards = [
    {
      icon: <Users size={24} />,
      iconClass: 'resumen-asistencia__icon--total',
      label: 'Total Estudiantes',
      value: MOCK_DATA.total,
      valueClass: '',
    },
    {
      icon: <CheckCircle size={24} />,
      iconClass: 'resumen-asistencia__icon--presente',
      label: 'Presentes Hoy',
      value: MOCK_DATA.presentes,
      valueClass: 'resumen-asistencia__value--presente',
    },
    {
      icon: <XCircle size={24} />,
      iconClass: 'resumen-asistencia__icon--ausente',
      label: 'Ausentes Hoy',
      value: MOCK_DATA.ausentes,
      valueClass: 'resumen-asistencia__value--ausente',
    },
    {
      icon: <Clock3 size={24} />,
      iconClass: 'resumen-asistencia__icon--porcentaje',
      label: '% Asistencia',
      value: `${MOCK_DATA.porcentaje}%`,
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