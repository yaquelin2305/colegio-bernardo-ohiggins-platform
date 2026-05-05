import { useState, useEffect } from 'react';
import { Users, GraduationCap, AlertTriangle, TrendingUp, CalendarCheck, MessageSquare } from 'lucide-react';
import { useOutletContext } from 'react-router-dom';
import { useAuth } from '../../../core/context/AuthContext';
import TarjetaKpi from '../components/TarjetaKpi';
import { obtenerKpisDashboard } from '../services/gestionAcademicaService';
import '../styles/DashboardPage.css';

const ICONOS = {
  Users:         <Users size={22} aria-hidden="true" />,
  GraduationCap: <GraduationCap size={22} aria-hidden="true" />,
  AlertTriangle: <AlertTriangle size={22} aria-hidden="true" />,
  TrendingUp:    <TrendingUp size={22} aria-hidden="true" />,
  CalendarCheck: <CalendarCheck size={22} aria-hidden="true" />,
  MessageSquare: <MessageSquare size={22} aria-hidden="true" />,
};

function DashboardPage() {
  const { setTitulo } = useOutletContext();
  const { usuario } = useAuth();

  useEffect(() => { setTitulo('Panel General'); }, [setTitulo]);
  const [kpis, setKpis] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const fechaHoy = new Date().toLocaleDateString('es-CL', {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });

  useEffect(() => {
    obtenerKpisDashboard(usuario?.rol)
      .then(setKpis)
      .catch(() => setError('No se pudieron cargar los indicadores.'))
      .finally(() => setIsLoading(false));
  }, [usuario?.rol]);

  return (
    <section className="dashboard__seccion">
      <div className="dashboard__bienvenida">
        <h2 className="dashboard__saludo">
          {usuario?.nombre ? `Bienvenido, ${usuario.nombre}` : 'Bienvenido'}
        </h2>
        <p className="dashboard__fecha">{fechaHoy}</p>
      </div>

      {isLoading && <p className="dashboard__cargando">Cargando...</p>}
      {error && <p className="dashboard__error">{error}</p>}

      {!isLoading && !error && (
        <div className="dashboard__stats">
          {kpis.map(kpi => (
            <TarjetaKpi key={kpi.label} {...kpi} icono={ICONOS[kpi.iconKey]} />
          ))}
        </div>
      )}
    </section>
  );
}

export default DashboardPage;
