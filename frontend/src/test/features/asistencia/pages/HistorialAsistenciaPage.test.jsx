import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import HistorialAsistenciaPage from '../../../../features/asistencia/pages/HistorialAsistenciaPage';
import * as s from '../../../../features/asistencia/services/asistenciaService';
import { useAuth } from '../../../../core/context/useAuth';
import axiosClient from '../../../../core/api/axiosClient';

vi.mock('../../../../core/context/useAuth');
vi.mock('../../../../features/asistencia/services/asistenciaService');
vi.mock('../../../../core/api/axiosClient');
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useOutletContext: () => ({ setTitulo: vi.fn() }) };
});

function renderPage() {
  return render(<BrowserRouter><HistorialAsistenciaPage /></BrowserRouter>);
}

describe('HistorialAsistenciaPage', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  it('muestra "Cargando..." mientras carga', () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'ADMIN' }, logout: vi.fn() });
    vi.mocked(s.obtenerAlumnos).mockReturnValue(new Promise(() => {}));
    renderPage();
    expect(screen.getByText('Cargando...')).toBeInTheDocument();
  });

  it('carga alumnos para rol ADMIN', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'ADMIN' }, logout: vi.fn() });
    vi.mocked(s.obtenerAlumnos).mockResolvedValue([{ id: 'e1', nombre: 'Juan', rut: '11-1', curso: '' }]);
    vi.mocked(s.obtenerHistorialAsistencia).mockResolvedValue([]);
    renderPage();
    await waitFor(() => {
      const matches = screen.getAllByText(/Selecciona un alumno/);
      expect(matches.length).toBeGreaterThanOrEqual(2);
    });
  });

  it('carga alumnos para rol DOCENTE', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'DOCENTE' }, logout: vi.fn() });
    vi.mocked(s.obtenerAlumnosPorDocente).mockResolvedValue([{ id: 'e1', nombre: 'Juan', rut: '11-1', curso: '1°A' }]);
    vi.mocked(s.obtenerHistorialAsistencia).mockResolvedValue([]);
    renderPage();
    await waitFor(() => {
      const matches = screen.getAllByText(/Selecciona un alumno/);
      expect(matches.length).toBeGreaterThanOrEqual(2);
    });
  });

  it('carga auto para ESTUDIANTE', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'ESTUDIANTE', userId: 'e1' }, logout: vi.fn() });
    vi.mocked(axiosClient.get).mockResolvedValue({ data: { nombreCompleto: 'Alumno Test', rut: '11-1', curso: '1°A' } });
    vi.mocked(s.obtenerHistorialAsistencia).mockResolvedValue([]);
    renderPage();
    await waitFor(() => expect(screen.getByText('Alumno Test')).toBeInTheDocument());
  });

  it('muestra error si falla la carga', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'ADMIN' }, logout: vi.fn() });
    vi.mocked(s.obtenerAlumnos).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('No se pudo cargar el listado de alumnos.')).toBeInTheDocument());
  });
});
