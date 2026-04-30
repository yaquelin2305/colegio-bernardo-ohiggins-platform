import { Users, TrendingUp, CalendarCheck, ClipboardList } from 'lucide-react';
import MainLayout from '../../../shared/components/layout/MainLayout';
import '../styles/DashboardPage.css';

const estadoActual = [
  {
    label: 'Total Alumnos',
    numero: '—',
    detalle: '',
    icono: <Users size={20} />,
    variante: 'primary',
  },
  {
    label: 'Promedio General',
    numero: '—',
    detalle: '',
    icono: <TrendingUp size={20} />,
    variante: 'accent',
  },
  {
    label: 'Asistencia Hoy',
    numero: '—',
    detalle: '',
    icono: <CalendarCheck size={20} />,
    variante: 'accent',
  },
  {
    label: 'Evaluaciones Pendientes',
    numero: '—',
    detalle: '',
    icono: <ClipboardList size={20} />,
    variante: 'warning',
  },
];

function DashboardPage() {
  const fechaHoy = new Date().toLocaleDateString('es-CL', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });

  return (
    <MainLayout titulo="Panel General">
      <section className="dashboard__seccion">
        <div className="dashboard__bienvenida">
          <h2 className="dashboard__saludo">Bienvenido</h2>
          <p className="dashboard__fecha">{fechaHoy}</p>
        </div>

        <div className="dashboard__stats">
          {estadoActual.map((stat) => (
            <article key={stat.label} className={`stat-card${stat.variante !== 'primary' ? ` stat-card--${stat.variante}` : ''}`}>
              <div className={`stat-card__icono-wrapper stat-card__icono-wrapper--${stat.variante}`}>
                {stat.icono}
              </div>
              <span className="stat-card__label">{stat.label}</span>
              <span className="stat-card__numero">{stat.numero}</span>
              <span className="stat-card__detalle">{stat.detalle}</span>
            </article>
          ))}
        </div>
      </section>
    </MainLayout>
  );
}

export default DashboardPage;
