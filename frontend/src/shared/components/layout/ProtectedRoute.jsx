import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../../../core/context/useAuth';

function ProtectedRoute({ children, allowedRoles }) {
  const { token, usuario } = useAuth();
  if (!token) return <Navigate to="/login" replace />;
  if (allowedRoles && usuario?.rol && !allowedRoles.includes(usuario.rol)) {
    const destino = ['APODERADO', 'ESTUDIANTE'].includes(usuario.rol)
      ? '/mis-calificaciones'
      : '/calificaciones';
    return <Navigate to={destino} replace />;
  }
  return children ?? <Outlet />;
}

export default ProtectedRoute;
