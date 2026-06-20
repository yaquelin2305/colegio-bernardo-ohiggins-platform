import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import VisualizadorNotasPage from '../../../../features/gestion-academica/pages/VisualizadorNotasPage';
import * as gestionAcademicaService from '../../../../features/gestion-academica/services/gestionAcademicaService';
import { useAuth } from '../../../../core/context/useAuth';

vi.mock('../../../../core/context/useAuth');
vi.mock('../../../../features/gestion-academica/services/gestionAcademicaService');
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useOutletContext: () => ({ setTitulo: vi.fn() }) };
});

function renderPage() {
  return render(<BrowserRouter><VisualizadorNotasPage /></BrowserRouter>);
}

describe('VisualizadorNotasPage', () => {
  it('muestra "Cargando..." mientras carga', () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'ESTUDIANTE' }, logout: vi.fn() });
    vi.mocked(gestionAcademicaService.obtenerBoletinPropio).mockReturnValue(new Promise(() => {}));
    renderPage();
    expect(screen.getByText('Cargando...')).toBeInTheDocument();
  });

  it('obtiene boletín propio para ESTUDIANTE', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'ESTUDIANTE' }, logout: vi.fn() });
    vi.mocked(gestionAcademicaService.obtenerBoletinPropio).mockResolvedValue({
      alumno: { nombre: 'Alumno Test', curso: '1°A', promedioGeneral: 6, porcentajeAsistencia: 85 },
      asignaturas: [{ nombre: 'Mat', nota1: 6, nota2: 5, nota3: 7, promedio: 6, estado: 'APROBADO' }],
    });
    renderPage();
    await waitFor(() => expect(screen.getByText('Alumno Test')).toBeInTheDocument());
    expect(screen.getByText('Mat')).toBeInTheDocument();
  });

  it('obtiene boletín del pupilo para APODERADO', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'APODERADO' }, logout: vi.fn() });
    vi.mocked(gestionAcademicaService.getPupiloUuidFromToken).mockReturnValue('pupil-uuid');
    vi.mocked(gestionAcademicaService.obtenerBoletinPupilo).mockResolvedValue({
      alumno: { nombre: 'Pupilo Test', curso: '2°B', promedioGeneral: 5.5, porcentajeAsistencia: 90 },
      asignaturas: [],
    });
    renderPage();
    await waitFor(() => expect(screen.getByText('Pupilo Test')).toBeInTheDocument());
  });

  it('muestra error si APODERADO no tiene pupilo', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'APODERADO' }, logout: vi.fn() });
    vi.mocked(gestionAcademicaService.getPupiloUuidFromToken).mockReturnValue(null);
    renderPage();
    await waitFor(() => expect(screen.getByText('No tienes un pupilo asociado a tu cuenta.')).toBeInTheDocument());
  });

  it('muestra error si falla carga', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'ESTUDIANTE' }, logout: vi.fn() });
    vi.mocked(gestionAcademicaService.obtenerBoletinPropio).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('No se pudo cargar el boletín de notas.')).toBeInTheDocument());
  });
});
