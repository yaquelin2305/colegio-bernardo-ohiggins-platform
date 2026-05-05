import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './core/context/AuthContext';
import ProtectedRoute from './shared/components/layout/ProtectedRoute';
import DashboardPage from './features/gestion-academica/pages/DashboardPage';
import RegistroNotasPage from './features/gestion-academica/pages/RegistroNotasPage';
import VisualizadorNotasPage from './features/gestion-academica/pages/VisualizadorNotasPage';
import GestionAcademicaAdminPage from './features/gestion-academica/pages/GestionAcademicaAdminPage';
import AsignacionDocentesPage from './features/gestion-academica/pages/AsignacionDocentesPage';
import ListadoEstudiantesCursoPage from './features/gestion-academica/pages/ListadoEstudiantesCursoPage';
import BandejaMensajesPage from './features/comunicaciones/pages/BandejaMensajesPage';
import RedactarMensajePage from './features/comunicaciones/pages/RedactarMensajePage';
import DetalleMensajePage from './features/comunicaciones/pages/DetalleMensajePage';
import AsistenciaPage from './features/asistencia/pages/AsistenciaPage';
import RegistroAnotacionesPage from './features/asistencia/pages/RegistroAnotacionesPage';
import HistorialAsistenciaPage from './features/asistencia/pages/HistorialAsistenciaPage';
import JustificacionInasistenciasPage from './features/asistencia/pages/JustificacionInasistenciasPage';
import GestionUsuariosPage from './features/usuarios/pages/GestionUsuariosPage';
import LoginPage from './features/auth/pages/LoginPage';

function App() {
  return (
    <AuthProvider>
      <Router>
        <Routes>
          <Route path="/" element={<Navigate to="/login" replace />} />
          <Route path="/login" element={<LoginPage />} />

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

          {/* ── Asistencia ── */}
          <Route path="/asistencia" element={
            <ProtectedRoute>
              <AsistenciaPage />
            </ProtectedRoute>
          } />
          <Route path="/asistencia/anotaciones" element={
            <ProtectedRoute>
              <RegistroAnotacionesPage />
            </ProtectedRoute>
          } />
          <Route path="/asistencia/historial" element={
            <ProtectedRoute>
              <HistorialAsistenciaPage />
            </ProtectedRoute>
          } />
          <Route path="/asistencia/justificar" element={
            <ProtectedRoute>
              <JustificacionInasistenciasPage />
            </ProtectedRoute>
          } />

          {/* ── Comunicaciones ── */}
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
          <Route path="/comunicaciones/:id" element={
            <ProtectedRoute>
              <DetalleMensajePage />
            </ProtectedRoute>
          } />

          {/* ── Administración ── */}
          <Route path="/admin/gestion-academica" element={
            <ProtectedRoute>
              <GestionAcademicaAdminPage />
            </ProtectedRoute>
          } />
          <Route path="/admin/asignacion-docentes" element={
            <ProtectedRoute>
              <AsignacionDocentesPage />
            </ProtectedRoute>
          } />
          <Route path="/admin/usuarios" element={
            <ProtectedRoute>
              <GestionUsuariosPage />
            </ProtectedRoute>
          } />
          <Route path="/cursos/:cursoId/estudiantes" element={
            <ProtectedRoute>
              <ListadoEstudiantesCursoPage />
            </ProtectedRoute>
          } />
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
