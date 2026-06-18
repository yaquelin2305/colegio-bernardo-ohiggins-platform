import { Navigate } from 'react-router-dom';
import { useAuth } from '../../../core/context/AuthContext';

function ProtectedRoute({ children, rolesPermitidos = [] }) {
  const { usuario } = useAuth();

  if (!usuario) {
    return <Navigate to="/login" replace />;
  }

  if (rolesPermitidos.length > 0 && !rolesPermitidos.includes(usuario.rol)) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

export default ProtectedRoute;
