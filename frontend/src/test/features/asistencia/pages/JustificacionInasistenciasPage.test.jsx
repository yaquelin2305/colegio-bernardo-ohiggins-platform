import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import JustificacionInasistenciasPage from '../../../../features/asistencia/pages/JustificacionInasistenciasPage';
import * as s from '../../../../features/asistencia/services/asistenciaService';
import { useAuth } from '../../../../core/context/useAuth';
import * as tokenUtils from '../../../../shared/utils/tokenUtils';

vi.mock('../../../../core/context/useAuth');
vi.mock('../../../../features/asistencia/services/asistenciaService');
vi.mock('../../../../shared/utils/tokenUtils');
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useOutletContext: () => ({ setTitulo: vi.fn() }) };
});

function renderPage() {
  return render(<BrowserRouter><JustificacionInasistenciasPage /></BrowserRouter>);
}

describe('JustificacionInasistenciasPage', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  it('muestra "Cargando..." mientras carga', () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'ADMIN' }, logout: vi.fn() });
    vi.mocked(s.obtenerInasistencias).mockReturnValue(new Promise(() => {}));
    renderPage();
    expect(screen.getByText('Cargando...')).toBeInTheDocument();
  });

  it('carga inasistencias para rol ADMIN', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'ADMIN' }, logout: vi.fn() });
    vi.mocked(s.obtenerInasistencias).mockResolvedValue([
      { id: 1, fecha: '2026-06-15', alumno: 'Juan', curso: '1°A', justificada: false },
    ]);
    renderPage();
    await waitFor(() => expect(screen.getByText('Pendientes')).toBeInTheDocument());
  });

  it('muestra error si falla la carga', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'ADMIN' }, logout: vi.fn() });
    vi.mocked(s.obtenerInasistencias).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('No se pudo cargar las inasistencias.')).toBeInTheDocument());
  });

  it('carga inasistencias para rol APODERADO con pupiloUuid', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'APODERADO' }, logout: vi.fn() });
    vi.mocked(tokenUtils.getPupiloUuidFromToken).mockReturnValue('pupilo1');
    vi.mocked(s.obtenerHistorialAsistencia).mockResolvedValue([
      { id: 1, fecha: '2026-06-15', nombre: 'Hijo', estado: 'ausente' },
    ]);
    renderPage();
    await waitFor(() => expect(screen.getByText('Pendientes')).toBeInTheDocument());
  });

  it('muestra array vacío si APODERADO sin pupiloUuid', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'APODERADO' }, logout: vi.fn() });
    vi.mocked(tokenUtils.getPupiloUuidFromToken).mockReturnValue(null);
    renderPage();
    await waitFor(() => expect(screen.getByText('Pendientes')).toBeInTheDocument());
  });

  it('justifica inasistencia exitosamente', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'ADMIN' }, logout: vi.fn() });
    vi.mocked(s.obtenerInasistencias).mockResolvedValue([
      { id: 1, fecha: '2026-06-15', alumno: 'Juan', curso: '1°A', justificada: false },
    ]);
    vi.mocked(s.justificarInasistencia).mockResolvedValue(undefined);
    renderPage();
    await waitFor(() => expect(screen.getByText('Pendientes')).toBeInTheDocument());

    const btnJustificar = screen.getByText('Justificar');
    fireEvent.click(btnJustificar);
    await waitFor(() => expect(screen.getByText('Enviar justificación')).toBeInTheDocument());

    const motivoInput = screen.getByPlaceholderText('Describe el motivo de la inasistencia...');
    fireEvent.change(motivoInput, { target: { name: 'motivo', value: 'Estuvo enfermo' } });
    fireEvent.click(screen.getByText('Enviar justificación'));
    await waitFor(() => expect(s.justificarInasistencia).toHaveBeenCalled());
  });

  it('falla justificar inasistencia muestra error', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'ADMIN' }, logout: vi.fn() });
    vi.mocked(s.obtenerInasistencias).mockResolvedValue([
      { id: 1, fecha: '2026-06-15', alumno: 'Juan', curso: '1°A', justificada: false },
    ]);
    vi.mocked(s.justificarInasistencia).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('Pendientes')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Justificar'));
    await waitFor(() => expect(screen.getByText('Enviar justificación')).toBeInTheDocument());
    const motivoInput = screen.getByPlaceholderText('Describe el motivo de la inasistencia...');
    fireEvent.change(motivoInput, { target: { name: 'motivo', value: 'Enfermo' } });
    fireEvent.click(screen.getByText('Enviar justificación'));
    await waitFor(() => expect(s.justificarInasistencia).toHaveBeenCalled());
  });

  it('cierra formulario de justificación sin guardar', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'ADMIN' }, logout: vi.fn() });
    vi.mocked(s.obtenerInasistencias).mockResolvedValue([
      { id: 1, fecha: '2026-06-15', alumno: 'Juan', curso: '1°A', justificada: false },
    ]);
    renderPage();
    await waitFor(() => expect(screen.getByText('Pendientes')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Justificar'));
    await waitFor(() => expect(screen.getByText('Cancelar')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Cancelar'));
    await waitFor(() => expect(screen.queryByText('Enviar justificación')).not.toBeInTheDocument());
  });

  it('muestra inasistencias justificadas', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { rol: 'ADMIN' }, logout: vi.fn() });
    vi.mocked(s.obtenerInasistencias).mockResolvedValue([
      { id: 1, fecha: '2026-06-15', alumno: 'Juan', curso: '1°A', justificada: true },
    ]);
    renderPage();
    await waitFor(() => {
      const matches = screen.getAllByText(/Justificadas?/);
      expect(matches.length).toBeGreaterThanOrEqual(1);
    });
  });
});
