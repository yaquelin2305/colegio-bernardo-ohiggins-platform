import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import GestionAcademicaAdminPage from '../../../../features/gestion-academica/pages/GestionAcademicaAdminPage';
import * as s from '../../../../features/gestion-academica/services/gestionAcademicaService';

vi.mock('../../../../features/gestion-academica/services/gestionAcademicaService');
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useOutletContext: () => ({ setTitulo: vi.fn() }) };
});

function renderPage() {
  return render(<BrowserRouter><GestionAcademicaAdminPage /></BrowserRouter>);
}

describe('GestionAcademicaAdminPage', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  it('muestra "Cargando..." mientras carga', () => {
    vi.mocked(s.obtenerCursos).mockReturnValue(new Promise(() => {}));
    renderPage();
    expect(screen.getByText('Cargando...')).toBeInTheDocument();
  });

  it('renderiza secciones de cursos y asignaturas tras carga exitosa', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A' }]);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue([{ id: 10, nombre: 'Mat' }]);
    renderPage();
    await waitFor(() => expect(screen.getByText('1°A')).toBeInTheDocument());
    expect(screen.getByText('Mat')).toBeInTheDocument();
  });

  it('muestra error si falla carga', async () => {
    vi.mocked(s.obtenerCursos).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('No se pudo cargar la información académica.')).toBeInTheDocument());
  });

  it('crea un curso exitosamente', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A', anioEscolar: 2026 }]);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue([{ id: 10, nombre: 'Mat' }]);
    vi.mocked(s.crearCurso).mockResolvedValue({ id: 2 });
    renderPage();
    await waitFor(() => expect(screen.getByText('Crear Curso')).toBeInTheDocument());
    const nombreInput = screen.getByPlaceholderText('Ej: 1° Medio A');
    fireEvent.change(nombreInput, { target: { name: 'nombre', value: '2°B' } });
    fireEvent.click(screen.getByText('Crear Curso'));
    await waitFor(() => expect(s.crearCurso).toHaveBeenCalled());
  });

  it('falla crear curso muestra error', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A', anioEscolar: 2026 }]);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue([{ id: 10, nombre: 'Mat' }]);
    vi.mocked(s.crearCurso).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('Crear Curso')).toBeInTheDocument());
    const nombreInput = screen.getByPlaceholderText('Ej: 1° Medio A');
    fireEvent.change(nombreInput, { target: { name: 'nombre', value: '2°B' } });
    fireEvent.click(screen.getByText('Crear Curso'));
    await waitFor(() => expect(s.crearCurso).toHaveBeenCalled());
  });

  it('crea una asignatura exitosamente', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A', anioEscolar: 2026 }]);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue([{ id: 10, nombre: 'Mat' }]);
    vi.mocked(s.crearAsignatura).mockResolvedValue({ id: 11 });
    renderPage();
    await waitFor(() => expect(screen.getByText('Asignaturas')).toBeInTheDocument());

    const nombreInputs = screen.getAllByRole('textbox');
    const asignaturaInput = nombreInputs.find(i => i.closest('section')?.textContent?.includes('Asignaturas')) || nombreInputs[2];
    fireEvent.change(asignaturaInput, { target: { name: 'nombre', value: 'Historia' } });

    const buttons = screen.getAllByText('Agregar Asignatura');
    fireEvent.click(buttons[0]);
    await waitFor(() => expect(s.crearAsignatura).toHaveBeenCalled());
  });

  it('falla crear asignatura muestra error', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A', anioEscolar: 2026 }]);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue([{ id: 10, nombre: 'Mat' }]);
    vi.mocked(s.crearAsignatura).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('Asignaturas')).toBeInTheDocument());
    const buttons = screen.getAllByText('Agregar Asignatura');
    fireEvent.click(buttons[0]);
    await waitFor(() => expect(s.crearAsignatura).toHaveBeenCalled());
  });

  it('renderiza tabla de cursos con botón de estudiantes', async () => {
    vi.mocked(s.obtenerCursos).mockResolvedValue([{ id: 1, nombre: '1°A', anioEscolar: 2026 }]);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue([{ id: 10, nombre: 'Mat' }]);
    renderPage();
    await waitFor(() => expect(screen.getByLabelText('Ver estudiantes del curso 1°A')).toBeInTheDocument());
  });
});
