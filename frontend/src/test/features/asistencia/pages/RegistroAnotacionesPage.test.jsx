import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import RegistroAnotacionesPage from '../../../../features/asistencia/pages/RegistroAnotacionesPage';
import * as s from '../../../../features/asistencia/services/asistenciaService';

vi.mock('../../../../features/asistencia/services/asistenciaService');
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useOutletContext: () => ({ setTitulo: vi.fn() }) };
});

function renderPage() {
  return render(<BrowserRouter><RegistroAnotacionesPage /></BrowserRouter>);
}

describe('RegistroAnotacionesPage', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  it('muestra "Cargando..." mientras carga', () => {
    vi.mocked(s.obtenerCursos).mockReturnValue(new Promise(() => {}));
    renderPage();
    expect(screen.getByText('Cargando...')).toBeInTheDocument();
  });

  it('carga cursos y selecciona el primero', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerAlumnosPorCurso).mockResolvedValue([]);
    renderPage();
    await waitFor(() => expect(screen.getByText('1°A')).toBeInTheDocument());
  });

  it('muestra error si falla carga', async () => {
    vi.mocked(s.obtenerCursos).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('No se pudo cargar el listado de cursos.')).toBeInTheDocument());
  });

  it('abre panel de nueva anotación al hacer click en Agregar', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerAlumnosPorCurso).mockResolvedValue([
      { id: 'e1', rut: '11-1', nombre: 'Juan' },
    ]);
    vi.mocked(s.obtenerAnotacionesPorEstudiante).mockResolvedValue([]);
    renderPage();
    await waitFor(() => expect(screen.getByText('Juan')).toBeInTheDocument());
    const btnAgregar = screen.getByText('Agregar');
    fireEvent.click(btnAgregar);
    await waitFor(() => expect(screen.getByText(/Nueva anotación para/)).toBeInTheDocument());
  });

  it('guarda anotación exitosamente', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerAlumnosPorCurso).mockResolvedValue([
      { id: 'e1', rut: '11-1', nombre: 'Juan' },
    ]);
    vi.mocked(s.obtenerAnotacionesPorEstudiante).mockResolvedValue([]);
    vi.mocked(s.guardarAnotacion).mockResolvedValue({ id: 'n1', tipo: 'positiva', descripcion: 'Bien' });
    renderPage();
    await waitFor(() => expect(screen.getByText('Juan')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Agregar'));
    await waitFor(() => expect(screen.getByText('Guardar anotación')).toBeInTheDocument());
    const descInput = screen.getByPlaceholderText('Ej: Participación destacada en clase...');
    fireEvent.change(descInput, { target: { value: 'Buen trabajo' } });
    fireEvent.click(screen.getByText('Guardar anotación'));
    await waitFor(() => expect(s.guardarAnotacion).toHaveBeenCalled());
  });

  it('falla guardar anotación muestra error', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerAlumnosPorCurso).mockResolvedValue([
      { id: 'e1', rut: '11-1', nombre: 'Juan' },
    ]);
    vi.mocked(s.obtenerAnotacionesPorEstudiante).mockResolvedValue([]);
    vi.mocked(s.guardarAnotacion).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('Juan')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Agregar'));
    await waitFor(() => expect(screen.getByText('Guardar anotación')).toBeInTheDocument());
    const descInput = screen.getByPlaceholderText('Ej: Participación destacada en clase...');
    fireEvent.change(descInput, { target: { value: 'Buen trabajo' } });
    fireEvent.click(screen.getByText('Guardar anotación'));
    await waitFor(() => expect(s.guardarAnotacion).toHaveBeenCalled());
  });

  it('cambia de curso y carga alumnos del nuevo curso', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([
      { id: 1, nombre: '1°A' },
      { id: 2, nombre: '2°B' },
    ]);
    vi.mocked(s.obtenerAlumnosPorCurso).mockResolvedValue([{ id: 'e1', rut: '11-1', nombre: 'Juan' }]);
    vi.mocked(s.obtenerAnotacionesPorEstudiante).mockResolvedValue([]);
    renderPage();
    await waitFor(() => expect(screen.getByText('1°A')).toBeInTheDocument());
    const select = screen.getByRole('combobox');
    fireEvent.change(select, { target: { value: '2' } });
    await waitFor(() => expect(s.obtenerAlumnosPorCurso).toHaveBeenCalledWith('2'));
  });

  it('maneja error al cargar alumnos por curso', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerAlumnosPorCurso).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('No se pudo cargar los alumnos del curso.')).toBeInTheDocument());
  });
});
