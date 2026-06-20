import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import DashboardPage from '../../../../features/gestion-academica/pages/DashboardPage';
import * as gestionAcademicaService from '../../../../features/gestion-academica/services/gestionAcademicaService';
import { useAuth } from '../../../../core/context/useAuth';

vi.mock('../../../../core/context/useAuth');
vi.mock('../../../../features/gestion-academica/services/gestionAcademicaService');
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useOutletContext: () => ({ setTitulo: vi.fn() }) };
});

function renderPage() {
  return render(<BrowserRouter><DashboardPage /></BrowserRouter>);
}

describe('DashboardPage', () => {
  it('muestra nombre del usuario', () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { nombre: 'Admin', rol: 'ADMIN' }, logout: vi.fn() });
    vi.mocked(gestionAcademicaService.obtenerKpisDashboard).mockReturnValue(new Promise(() => {}));
    renderPage();
    expect(screen.getByText('Bienvenido, Admin')).toBeInTheDocument();
  });

  it('muestra "Bienvenido" sin nombre si no hay', () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'ADMIN' }, logout: vi.fn() });
    vi.mocked(gestionAcademicaService.obtenerKpisDashboard).mockReturnValue(new Promise(() => {}));
    renderPage();
    expect(screen.getByText('Bienvenido')).toBeInTheDocument();
  });

  it('muestra "Cargando..." mientras carga KPIs', () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { nombre: 'Admin', rol: 'ADMIN' }, logout: vi.fn() });
    vi.mocked(gestionAcademicaService.obtenerKpisDashboard).mockReturnValue(new Promise(() => {}));
    renderPage();
    expect(screen.getByText('Cargando...')).toBeInTheDocument();
  });

  it('renderiza KPIs cuando carga exitosa', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { nombre: 'Admin', rol: 'ADMIN' }, logout: vi.fn() });
    vi.mocked(gestionAcademicaService.obtenerKpisDashboard).mockResolvedValue([
      { label: 'Estudiantes', numero: 500, iconKey: 'Users' },
    ]);
    renderPage();
    await waitFor(() => expect(screen.getByText('500')).toBeInTheDocument());
  });

  it('muestra error si falla carga de KPIs', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { nombre: 'Admin', rol: 'ADMIN' }, logout: vi.fn() });
    vi.mocked(gestionAcademicaService.obtenerKpisDashboard).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('No se pudieron cargar los indicadores.')).toBeInTheDocument());
  });
});
