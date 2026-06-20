import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import App from '../App';

vi.mock('../core/context/AuthContext', () => {
  const AuthContext = { Provider: ({ children }) => <>{children}</>, Consumer: ({ children }) => <>{children}</> };
  return {
    AuthProvider: ({ children }) => <>{children}</>,
    AuthContext,
  };
});

vi.mock('../core/context/useAuth', () => ({
  useAuth: () => ({ usuario: null, token: null, login: vi.fn(), logout: vi.fn() }),
}));

vi.mock('../shared/components/layout/ProtectedRoute', () => ({
  default: ({ children }) => <>{children}</>,
}));

vi.mock('../shared/components/layout/MainLayout', () => ({
  default: ({ children }) => <>{children}</>,
}));

vi.mock('../features/auth/pages/LoginPage', () => ({
  default: () => <div>LoginPageContent</div>,
}));

vi.mock('../features/gestion-academica/pages/DashboardPage', () => ({
  default: () => <div>DashboardPageContent</div>,
}));

vi.mock('../features/gestion-academica/pages/RegistroNotasPage', () => ({
  default: () => <div>RegistroNotasPageContent</div>,
}));

vi.mock('../features/gestion-academica/pages/VisualizadorNotasPage', () => ({
  default: () => <div>VisualizadorNotasPageContent</div>,
}));

vi.mock('../features/gestion-academica/pages/GestionAcademicaAdminPage', () => ({
  default: () => <div>GestionAcademicaAdminPageContent</div>,
}));

vi.mock('../features/gestion-academica/pages/AsignacionDocentesPage', () => ({
  default: () => <div>AsignacionDocentesPageContent</div>,
}));

vi.mock('../features/gestion-academica/pages/ListadoEstudiantesCursoPage', () => ({
  default: () => <div>ListadoEstudiantesCursoPageContent</div>,
}));

vi.mock('../features/comunicaciones/pages/BandejaMensajesPage', () => ({
  default: () => <div>BandejaMensajesPageContent</div>,
}));

vi.mock('../features/comunicaciones/pages/RedactarMensajePage', () => ({
  default: () => <div>RedactarMensajePageContent</div>,
}));

vi.mock('../features/comunicaciones/pages/DetalleMensajePage', () => ({
  default: () => <div>DetalleMensajePageContent</div>,
}));

vi.mock('../features/asistencia/pages/AsistenciaPage', () => ({
  default: () => <div>AsistenciaPageContent</div>,
}));

vi.mock('../features/asistencia/pages/RegistroAnotacionesPage', () => ({
  default: () => <div>RegistroAnotacionesPageContent</div>,
}));

vi.mock('../features/asistencia/pages/HistorialAsistenciaPage', () => ({
  default: () => <div>HistorialAsistenciaPageContent</div>,
}));

vi.mock('../features/asistencia/pages/JustificacionInasistenciasPage', () => ({
  default: () => <div>JustificacionInasistenciasPageContent</div>,
}));

vi.mock('../features/usuarios/pages/GestionUsuariosPage', () => ({
  default: () => <div>GestionUsuariosPageContent</div>,
}));

vi.mock('../shared/components/NotFoundPage', () => ({
  default: () => <div>NotFoundPageContent</div>,
}));

describe('App', () => {
  beforeEach(() => {
    window.history.pushState({}, '', '/');
  });

  it('renderiza sin errores', () => {
    expect(() => render(<App />)).not.toThrow();
  });

  it('redirige / a /login mostrando LoginPage', async () => {
    render(<App />);
    await waitFor(() => {
      expect(screen.getByText('LoginPageContent')).toBeInTheDocument();
    });
  });

  it('renderiza NotFoundPage para rutas desconocidas', async () => {
    window.history.pushState({}, '', '/ruta-que-no-existe');
    window.dispatchEvent(new PopStateEvent('popstate'));
    render(<App />);
    await waitFor(() => {
      expect(screen.getByText('NotFoundPageContent')).toBeInTheDocument();
    });
  });
});
