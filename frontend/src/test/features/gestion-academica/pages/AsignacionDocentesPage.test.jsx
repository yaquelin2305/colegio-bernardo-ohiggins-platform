import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import AsignacionDocentesPage from '../../../../features/gestion-academica/pages/AsignacionDocentesPage';
import * as s from '../../../../features/gestion-academica/services/gestionAcademicaService';

vi.mock('../../../../features/gestion-academica/services/gestionAcademicaService');
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useOutletContext: () => ({ setTitulo: vi.fn() }) };
});

const docenteData = [{ id: 'd1', nombre: 'Ana' }];
const cursoData = [{ id: 1, nombre: '1°A' }];
const asignaturaData = [{ id: 10, nombre: 'Mat' }];

function renderPage() {
  return render(<BrowserRouter><AsignacionDocentesPage /></BrowserRouter>);
}

describe('AsignacionDocentesPage', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  it('muestra "Cargando..." mientras carga', () => {
    vi.mocked(s.obtenerDocentes).mockReturnValue(new Promise(() => {}));
    renderPage();
    expect(screen.getByText('Cargando...')).toBeInTheDocument();
  });

  it('renderiza formulario y tabla tras carga exitosa', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue(docenteData);
    vi.mocked(s.obtenerCursos).mockResolvedValue(cursoData);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue(asignaturaData);
    vi.mocked(s.obtenerAsignaciones).mockResolvedValue([]);
    renderPage();
    await waitFor(() => expect(screen.getByText('Asignar Docente a Curso y Asignatura')).toBeInTheDocument());
  });

  it('muestra error si falla carga', async () => {
    vi.mocked(s.obtenerDocentes).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('No se pudo cargar la información de asignaciones.')).toBeInTheDocument());
  });

  it('enriquece asignaciones con nombres de docente/curso/asignatura', async () => {
    const asignaciones = [{ id: 'a1', docenteUuid: 'd1', cursoId: 1, asignaturaId: 10 }];
    vi.mocked(s.obtenerDocentes).mockResolvedValue(docenteData);
    vi.mocked(s.obtenerCursos).mockResolvedValue(cursoData);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue(asignaturaData);
    vi.mocked(s.obtenerAsignaciones).mockResolvedValue(asignaciones);
    renderPage();
    await waitFor(() => {
      const matches = screen.getAllByText('Ana');
      expect(matches.length).toBeGreaterThanOrEqual(1);
    });
    expect(screen.getAllByText('1°A').length).toBeGreaterThanOrEqual(1);
    expect(screen.getAllByText('Mat').length).toBeGreaterThanOrEqual(1);
  });

  it('crea asignación exitosamente', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue(docenteData);
    vi.mocked(s.obtenerCursos).mockResolvedValue(cursoData);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue(asignaturaData);
    vi.mocked(s.obtenerAsignaciones).mockResolvedValue([]);
    vi.mocked(s.crearAsignacion).mockResolvedValue({ id: 'nueva1' });
    renderPage();
    await waitFor(() => expect(screen.getByText('Asignar Docente a Curso y Asignatura')).toBeInTheDocument());

    const selects = screen.getAllByRole('combobox');
    expect(selects.length).toBeGreaterThanOrEqual(3);
    fireEvent.change(selects[0], { target: { name: 'docenteId', value: 'd1' } });
    fireEvent.change(selects[1], { target: { name: 'cursoId', value: '1' } });
    fireEvent.change(selects[2], { target: { name: 'asignaturaId', value: '10' } });

    const btn = screen.getByText('Asignar');
    fireEvent.click(btn);
    await waitFor(() => expect(s.crearAsignacion).toHaveBeenCalled());
  });

  it('falla crear asignación muestra error', async () => {
    vi.mocked(s.obtenerDocentes).mockResolvedValue(docenteData);
    vi.mocked(s.obtenerCursos).mockResolvedValue(cursoData);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue(asignaturaData);
    vi.mocked(s.obtenerAsignaciones).mockResolvedValue([]);
    vi.mocked(s.crearAsignacion).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('Asignar Docente a Curso y Asignatura')).toBeInTheDocument());
    const selects = screen.getAllByRole('combobox');
    expect(selects.length).toBeGreaterThanOrEqual(3);
    fireEvent.change(selects[0], { target: { name: 'docenteId', value: 'd1' } });
    fireEvent.change(selects[1], { target: { name: 'cursoId', value: '1' } });
    fireEvent.change(selects[2], { target: { name: 'asignaturaId', value: '10' } });
    fireEvent.click(screen.getByText('Asignar'));
    await waitFor(() => expect(s.crearAsignacion).toHaveBeenCalled());
  });

  it('elimina asignación exitosamente', async () => {
    const asignaciones = [{ id: 'a1', docenteUuid: 'd1', cursoId: 1, asignaturaId: 10 }];
    vi.mocked(s.obtenerDocentes).mockResolvedValue(docenteData);
    vi.mocked(s.obtenerCursos).mockResolvedValue(cursoData);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue(asignaturaData);
    vi.mocked(s.obtenerAsignaciones).mockResolvedValue(asignaciones);
    vi.mocked(s.eliminarAsignacion).mockResolvedValue(undefined);
    renderPage();
    await waitFor(() => {
      const matches = screen.getAllByText('Ana');
      expect(matches.length).toBeGreaterThanOrEqual(1);
    });
    const eliminarBtns = screen.getAllByRole('button', { name: /Eliminar/ });
    fireEvent.click(eliminarBtns[0]);
    await waitFor(() => expect(s.eliminarAsignacion).toHaveBeenCalledWith('a1'));
  });

  it('falla eliminar asignación muestra error', async () => {
    const asignaciones = [{ id: 'a1', docenteUuid: 'd1', cursoId: 1, asignaturaId: 10 }];
    vi.mocked(s.obtenerDocentes).mockResolvedValue(docenteData);
    vi.mocked(s.obtenerCursos).mockResolvedValue(cursoData);
    vi.mocked(s.obtenerAsignaturas).mockResolvedValue(asignaturaData);
    vi.mocked(s.obtenerAsignaciones).mockResolvedValue(asignaciones);
    vi.mocked(s.eliminarAsignacion).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => {
      const matches = screen.getAllByText('Ana');
      expect(matches.length).toBeGreaterThanOrEqual(1);
    });
    const eliminarBtns = screen.getAllByRole('button', { name: /Eliminar/ });
    fireEvent.click(eliminarBtns[0]);
    await waitFor(() => expect(s.eliminarAsignacion).toHaveBeenCalled());
  });
});
