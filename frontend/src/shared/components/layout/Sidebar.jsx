import { NavLink } from 'react-router-dom';
import { LayoutDashboard, BookOpen, CalendarCheck, MessageSquare, Settings, ClipboardList, UserCheck, Users, History, FileCheck } from 'lucide-react';
import { useAuth } from '../../../core/context/AuthContext';
import '../../styles/Sidebar.css';

const navLinks = [
  { to: '/dashboard',                 label: 'Dashboard',          roles: ['ADMIN', 'DOCENTE'],                               icono: <LayoutDashboard size={18} /> },
  { to: '/calificaciones',            label: 'Calificaciones',     roles: ['ADMIN', 'DOCENTE'],                               icono: <BookOpen size={18} /> },
  { to: '/mis-calificaciones',        label: 'Mis Calificaciones', roles: ['APODERADO', 'ESTUDIANTE'],                        icono: <ClipboardList size={18} /> },
  { to: '/asistencia/anotaciones',    label: 'Anotaciones',        roles: ['ADMIN', 'DOCENTE'],                               icono: <CalendarCheck size={18} /> },
  { to: '/asistencia/historial',      label: 'Historial',          roles: ['ADMIN', 'DOCENTE', 'APODERADO', 'ESTUDIANTE'],    icono: <History size={18} /> },
  { to: '/asistencia/justificar',     label: 'Justificar',         roles: ['ADMIN', 'APODERADO'],                             icono: <FileCheck size={18} /> },
  { to: '/comunicaciones',            label: 'Comunicaciones',     roles: ['ADMIN', 'DOCENTE', 'APODERADO', 'ESTUDIANTE'],    icono: <MessageSquare size={18} /> },
  { to: '/admin/gestion-academica',   label: 'Gestión Académica',  roles: ['ADMIN'],                                          icono: <Settings size={18} /> },
  { to: '/admin/asignacion-docentes', label: 'Asig. Docentes',     roles: ['ADMIN'],                                          icono: <UserCheck size={18} /> },
  { to: '/admin/usuarios',            label: 'Usuarios',           roles: ['ADMIN'],                                          icono: <Users size={18} /> },
];

function Sidebar() {
  const { usuario } = useAuth();
  const enlacesFiltrados = navLinks.filter(link =>
    !usuario?.rol || link.roles.includes(usuario.rol)
  );

  return (
    <aside className="sidebar">
      <div className="sidebar__marca">
        <p className="sidebar__marca-titulo">Colegio Bernardo O'higgins</p>
        <p className="sidebar__marca-subtitulo">Gestión Académica</p>
      </div>

      <nav className="sidebar__nav" aria-label="Navegación principal">
        {enlacesFiltrados.map(({ to, label, icono }) => (
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
