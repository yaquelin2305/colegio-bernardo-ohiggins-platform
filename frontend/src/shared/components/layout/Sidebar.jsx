import { NavLink } from 'react-router-dom';
import { LayoutDashboard, BookOpen, CalendarCheck, MessageSquare } from 'lucide-react';
import '../../styles/Sidebar.css';

const navLinks = [
  { to: '/dashboard',      label: 'Dashboard',      icono: <LayoutDashboard size={18} /> },
  { to: '/calificaciones', label: 'Calificaciones', icono: <BookOpen size={18} /> },
  { to: '/asistencia',     label: 'Asistencia',     icono: <CalendarCheck size={18} /> },
  { to: '/comunicaciones', label: 'Comunicaciones', icono: <MessageSquare size={18} /> },
];

function Sidebar() {
  return (
    <aside className="sidebar">
      <div className="sidebar__marca">
        <p className="sidebar__marca-titulo">Colegio Bernardo O'higgins</p>
        <p className="sidebar__marca-subtitulo">Gestión Académica</p>
      </div>

      <nav className="sidebar__nav" aria-label="Navegación principal">
        {navLinks.map(({ to, label, icono }) => (
          <NavLink
            key={to}
            to={to}
            className={({ isActive }) => `sidebar__link${isActive ? ' active' : ''}`}
          >
            {icono}
            {label}
          </NavLink>
        ))}
      </nav>
    </aside>
  );
}

export default Sidebar;
