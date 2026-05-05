import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './core/context/AuthContext';
import ProtectedRoute from './shared/components/layout/ProtectedRoute';
import MainLayout from './shared/components/layout/MainLayout';
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

          <Route element={<ProtectedRoute><MainLayout /></ProtectedRoute>}>
            <Route path="dashboard" element={<DashboardPage />} />
            <Route path="calificaciones" element={<RegistroNotasPage />} />
            <Route path="mis-calificaciones" element={<VisualizadorNotasPage />} />

            <Route path="asistencia" element={<AsistenciaPage />} />
            <Route path="asistencia/anotaciones" element={<RegistroAnotacionesPage />} />
            <Route path="asistencia/historial" element={<HistorialAsistenciaPage />} />
            <Route path="asistencia/justificar" element={<JustificacionInasistenciasPage />} />

            <Route path="comunicaciones" element={<BandejaMensajesPage />} />
            <Route path="comunicaciones/redactar" element={<RedactarMensajePage />} />
            <Route path="comunicaciones/:id" element={<DetalleMensajePage />} />

            <Route path="admin/gestion-academica" element={<GestionAcademicaAdminPage />} />
            <Route path="admin/asignacion-docentes" element={<AsignacionDocentesPage />} />
            <Route path="admin/usuarios" element={<GestionUsuariosPage />} />
            <Route path="cursos/:cursoId/estudiantes" element={<ListadoEstudiantesCursoPage />} />
          </Route>
        </Routes>
      </Router>
    </AuthProvider>
  );
}

export default App;
