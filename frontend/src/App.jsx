import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './core/context/AuthContext';
import ProtectedRoute from './shared/components/layout/ProtectedRoute';
import RegisterPage from './features/auth/pages/RegisterPage';
import DashboardPage from './features/gestion-academica/pages/DashboardPage';
import RegistroNotasPage from './features/gestion-academica/pages/RegistroNotasPage';
import VisualizadorNotasPage from './features/gestion-academica/pages/VisualizadorNotasPage';
import BandejaMensajesPage from './features/comunicaciones/pages/BandejaMensajesPage';
import RedactarMensajePage from './features/comunicaciones/pages/RedactarMensajePage';
import GestionAcademicaAdminPage from './features/gestion-academica/pages/GestionAcademicaAdminPage';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="/registro" element={<RegisterPage />} />

          <Route path="/dashboard" element={
            <ProtectedRoute>
              <DashboardPage />
            </ProtectedRoute>
          } />

          <Route path="/calificaciones" element={
            <ProtectedRoute>
              <RegistroNotasPage />
            </ProtectedRoute>
          } />

          <Route path="/mis-calificaciones" element={
            <ProtectedRoute>
              <VisualizadorNotasPage />
            </ProtectedRoute>
          } />

          <Route path="/comunicaciones" element={
            <ProtectedRoute>
              <BandejaMensajesPage />
            </ProtectedRoute>
          } />

          <Route path="/comunicaciones/redactar" element={
            <ProtectedRoute>
              <RedactarMensajePage />
            </ProtectedRoute>
          } />

          <Route path="/admin/gestion-academica" element={
            <ProtectedRoute>
              <GestionAcademicaAdminPage />
            </ProtectedRoute>
          } />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
