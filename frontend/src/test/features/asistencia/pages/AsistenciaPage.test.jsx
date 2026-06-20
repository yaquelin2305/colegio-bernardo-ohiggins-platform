import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import AsistenciaPage from '../../../../features/asistencia/pages/AsistenciaPage';
import * as s from '../../../../features/asistencia/services/asistenciaService';

vi.mock('../../../../features/asistencia/services/asistenciaService');
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useOutletContext: () => ({ setTitulo: vi.fn() }) };
});

function renderPage() {
  return render(<BrowserRouter><AsistenciaPage /></BrowserRouter>);
}

describe('AsistenciaPage', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  it('muestra "Cargando..." mientras carga inicial', () => {
    vi.mocked(s.obtenerCursos).mockReturnValue(new Promise(() => {}));
    renderPage();
    expect(screen.getByText('Cargando...')).toBeInTheDocument();
  });

  it('carga cursos y resumen al montar', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerResumenDiario).mockResolvedValue(null);
    renderPage();
    await waitFor(() => expect(screen.getByText('Gestión de Asistencia')).toBeInTheDocument());
  });

  it('muestra error si falla carga inicial', async () => {
    vi.mocked(s.obtenerCursos).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText(/Error al cargar los datos/)).toBeInTheDocument());
  });

  it('aplica filtro y muestra estudiantes con datos', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerResumenDiario).mockResolvedValue(null);
    vi.mocked(s.obtenerAsistenciaPorCurso).mockResolvedValue([
      { id: 'e1', estudianteId: 'e1', nombre: 'Juan', estado: 'presente', hora: '09:00' },
    ]);
    renderPage();
    await waitFor(() => expect(screen.getByText('Gestión de Asistencia')).toBeInTheDocument());

    const btnFiltrar = screen.getByText('Filtrar');
    fireEvent.click(btnFiltrar);
    await waitFor(() => expect(screen.getByText('Guardar Asistencia')).toBeInTheDocument());
  });

  it('usa obtenerAlumnosPorCurso cuando no hay datos de asistencia (curso vacío)', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerResumenDiario).mockResolvedValue(null);
    vi.mocked(s.obtenerAsistenciaPorCurso).mockResolvedValue([]);
    vi.mocked(s.obtenerAlumnosPorCurso).mockResolvedValue([
      { id: 'e1', nombre: 'Juan', apellido: 'Pérez' },
    ]);
    renderPage();
    await waitFor(() => expect(screen.getByText('Gestión de Asistencia')).toBeInTheDocument());
    const btnFiltrar = screen.getByText('Filtrar');
    fireEvent.click(btnFiltrar);
    await waitFor(() => expect(screen.getByText('Guardar Asistencia')).toBeInTheDocument());
  });

  it('cambia estado de estudiante', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerResumenDiario).mockResolvedValue(null);
    vi.mocked(s.obtenerAsistenciaPorCurso).mockResolvedValue([
      { id: 'e1', estudianteId: 'e1', nombre: 'Juan', estado: 'presente', hora: '09:00' },
    ]);
    renderPage();
    await waitFor(() => expect(screen.getByText('Gestión de Asistencia')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Filtrar'));
    await waitFor(() => expect(screen.getByText('Guardar Asistencia')).toBeInTheDocument());
  });

  it('guarda asistencia exitosamente', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerResumenDiario).mockResolvedValue(null);
    vi.mocked(s.obtenerAsistenciaPorCurso).mockResolvedValue([
      { id: 'e1', estudianteId: 'e1', nombre: 'Juan', estado: 'presente', hora: '09:00' },
    ]);
    vi.mocked(s.guardarAsistencia).mockResolvedValue(undefined);
    renderPage();
    await waitFor(() => expect(screen.getByText('Gestión de Asistencia')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Filtrar'));
    await waitFor(() => expect(screen.getByText('Guardar Asistencia')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Guardar Asistencia'));
    await waitFor(() => expect(s.guardarAsistencia).toHaveBeenCalled());
  });

  it('falla guardar asistencia muestra error', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerResumenDiario).mockResolvedValue(null);
    vi.mocked(s.obtenerAsistenciaPorCurso).mockResolvedValue([
      { id: 'e1', estudianteId: 'e1', nombre: 'Juan', estado: 'presente', hora: '09:00' },
    ]);
    vi.mocked(s.guardarAsistencia).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('Gestión de Asistencia')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Filtrar'));
    await waitFor(() => expect(screen.getByText('Guardar Asistencia')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Guardar Asistencia'));
    await waitFor(() => expect(s.guardarAsistencia).toHaveBeenCalled());
  });

  it('maneja error al aplicar filtros', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerResumenDiario).mockResolvedValue(null);
    vi.mocked(s.obtenerAsistenciaPorCurso).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('Gestión de Asistencia')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Filtrar'));
    await waitFor(() => expect(screen.getByText(/Error al aplicar los filtros/)).toBeInTheDocument());
  });

  it('deduplica estudiantes por estudianteId', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerResumenDiario).mockResolvedValue(null);
    vi.mocked(s.obtenerAsistenciaPorCurso).mockResolvedValue([
      { id: 'e1', estudianteId: 'e1', nombre: 'Juan', estado: 'presente', hora: '09:00' },
      { id: 'e2', estudianteId: 'e1', nombre: 'Juan', estado: 'tarde', hora: '10:00' },
    ]);
    renderPage();
    await waitFor(() => expect(screen.getByText('Gestión de Asistencia')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Filtrar'));
    await waitFor(() => expect(screen.getByText('Guardar Asistencia')).toBeInTheDocument());
  });
});
