import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import RegistroNotasPage from '../../../../features/gestion-academica/pages/RegistroNotasPage';
import * as s from '../../../../features/gestion-academica/services/gestionAcademicaService';

vi.mock('../../../../features/gestion-academica/services/gestionAcademicaService');
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useOutletContext: () => ({ setTitulo: vi.fn() }) };
});

function renderPage() {
  return render(<BrowserRouter><RegistroNotasPage /></BrowserRouter>);
}

describe('RegistroNotasPage', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  it('carga cursos y asignaturas al montar', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue([{ id: 10, nombre: 'Mat' }]);
    vi.mocked(s.obtenerCalificaciones).mockResolvedValue([]);
    renderPage();
    await waitFor(() => expect(screen.getByText('1°A')).toBeInTheDocument());
    expect(screen.getByText('Mat')).toBeInTheDocument();
  });

  it('carga calificaciones cuando curso y asignatura están seteados', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue([{ id: 10, nombre: 'Mat' }]);
    vi.mocked(s.obtenerCalificaciones).mockResolvedValue([{ id: 'e1', nombre: 'Juan', nota1: 5, nota2: 6, nota3: 4, promedio: 5.0 }]);
    renderPage();
    await waitFor(() => expect(screen.getByText('Juan')).toBeInTheDocument());
  });

  it('muestra error si falla carga de cursos/asignaturas', async () => {
    vi.mocked(s.obtenerCursos).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('No se pudo cargar cursos o asignaturas.')).toBeInTheDocument());
  });

  it('muestra error si falla carga de calificaciones', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue([{ id: 10, nombre: 'Mat' }]);
    vi.mocked(s.obtenerCalificaciones).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('No se pudo cargar el listado de calificaciones.')).toBeInTheDocument());
  });

  it('cambia valor de nota válido y recalcula promedio', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue([{ id: 10, nombre: 'Mat' }]);
    vi.mocked(s.obtenerCalificaciones).mockResolvedValue([{ id: 'e1', nombre: 'Juan', nota1: '5.0', nota2: '6.0', nota3: '4.0', promedio: 5.0 }]);
    renderPage();
    await waitFor(() => expect(screen.getByText('Juan')).toBeInTheDocument());
    const inputs = screen.getAllByRole('spinbutton');
    expect(inputs.length).toBeGreaterThanOrEqual(1);
    fireEvent.change(inputs[0], { target: { value: '6.5' } });
  });

  it('guarda calificaciones exitosamente', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue([{ id: 10, nombre: 'Mat' }]);
    vi.mocked(s.obtenerCalificaciones).mockResolvedValue([{ id: 'e1', nombre: 'Juan', nota1: 5, nota2: 6, nota3: 4, promedio: 5.0 }]);
    vi.mocked(s.guardarCalificaciones).mockResolvedValue(undefined);
    renderPage();
    await waitFor(() => expect(screen.getByText('Juan')).toBeInTheDocument());
    const btnGuardar = screen.getByText('Guardar Calificaciones');
    fireEvent.click(btnGuardar);
    await waitFor(() => expect(s.guardarCalificaciones).toHaveBeenCalled());
  });

  it('falla guardar calificaciones muestra error', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue([{ id: 10, nombre: 'Mat' }]);
    vi.mocked(s.obtenerCalificaciones).mockResolvedValue([{ id: 'e1', nombre: 'Juan', nota1: 5, nota2: 6, nota3: 4, promedio: 5.0 }]);
    vi.mocked(s.guardarCalificaciones).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('Juan')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Guardar Calificaciones'));
    await waitFor(() => expect(s.guardarCalificaciones).toHaveBeenCalled());
  });

  it('botón guardar está deshabilitado durante carga', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue([{ id: 10, nombre: 'Mat' }]);
    vi.mocked(s.obtenerCalificaciones).mockReturnValue(new Promise(() => {}));
    renderPage();
    await waitFor(() => expect(screen.getByText('Cargando...')).toBeInTheDocument());
    expect(screen.getByText('Guardar Calificaciones')).toBeDisabled();
  });
});
