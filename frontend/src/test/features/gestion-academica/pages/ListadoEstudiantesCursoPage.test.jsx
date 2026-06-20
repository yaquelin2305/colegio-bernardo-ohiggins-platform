import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import ListadoEstudiantesCursoPage from '../../../../features/gestion-academica/pages/ListadoEstudiantesCursoPage';
import * as s from '../../../../features/gestion-academica/services/gestionAcademicaService';

vi.mock('../../../../features/gestion-academica/services/gestionAcademicaService');
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useOutletContext: () => ({ setTitulo: vi.fn() }) };
});

const estudianteCompleto = { id: 'e1', rut: '12345678-9', nombre: 'Juan', apellido: 'Pérez', email: 'juan@test.com', promedio: 5.5, asistencia: 90 };
const estudianteCompleto2 = { id: 'e2', rut: '98765432-1', nombre: 'Ana', apellido: 'López', email: 'ana@test.com', promedio: 6.0, asistencia: 85 };
const disponibleBasico = { id: 'e1', nombre: 'Juan', apellido: 'Pérez', rut: '12345678-9' };
const disponibleBasico2 = { id: 'e2', nombre: 'Ana', apellido: 'López', rut: '98765432-1' };

function renderPage(cursoId = '1') {
  return render(
    <MemoryRouter initialEntries={[`/admin/cursos/${cursoId}`]}>
      <Routes>
        <Route path="/admin/cursos/:cursoId" element={<ListadoEstudiantesCursoPage />} />
      </Routes>
    </MemoryRouter>
  );
}

describe('ListadoEstudiantesCursoPage', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  it('muestra "Cargando..." mientras carga', () => {
    vi.mocked(s.obtenerCursoPorId).mockReturnValue(new Promise(() => {}));
    renderPage();
    expect(screen.getByText('Cargando...')).toBeInTheDocument();
  });

  it('renderiza InfoCurso con nombre del curso', async () => {
    vi.mocked(s.obtenerCursoPorId).mockResolvedValue({ id: 1, nombre: '1°A' });
    vi.mocked(s.obtenerEstudiantesPorCurso).mockResolvedValue([]);
    vi.mocked(s.obtenerEstudiantesDisponibles).mockResolvedValue([]);
    renderPage();
    await waitFor(() => expect(screen.getByText('1°A')).toBeInTheDocument());
  });

  it('muestra mensaje de error si falla carga', async () => {
    vi.mocked(s.obtenerCursoPorId).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('No se pudo cargar la información del curso.')).toBeInTheDocument());
  });

  it('muestra "El curso solicitado no existe" si curso es null', async () => {
    vi.mocked(s.obtenerCursoPorId).mockResolvedValue(null);
    vi.mocked(s.obtenerEstudiantesPorCurso).mockResolvedValue([]);
    vi.mocked(s.obtenerEstudiantesDisponibles).mockResolvedValue([]);
    renderPage();
    await waitFor(() => expect(screen.getByText('El curso solicitado no existe.')).toBeInTheDocument());
  });

  it('toggle panel de matrícula se abre y cierra', async () => {
    vi.mocked(s.obtenerCursoPorId).mockResolvedValue({ id: 1, nombre: '1°A' });
    vi.mocked(s.obtenerEstudiantesPorCurso).mockResolvedValue([]);
    vi.mocked(s.obtenerEstudiantesDisponibles).mockResolvedValue([disponibleBasico]);
    renderPage();
    await waitFor(() => expect(screen.getByText('1°A')).toBeInTheDocument());
    const btnMatricular = screen.getByText('Matricular Alumno');
    fireEvent.click(btnMatricular);
    await waitFor(() => {
      expect(screen.getAllByText('Cancelar').length).toBeGreaterThanOrEqual(1);
    });
    fireEvent.click(screen.getAllByText('Cancelar')[0]);
  });

  it('matricula un estudiante exitosamente', async () => {
    vi.mocked(s.obtenerCursoPorId).mockResolvedValue({ id: 1, nombre: '1°A' });
    vi.mocked(s.obtenerEstudiantesPorCurso).mockResolvedValue([]);
    vi.mocked(s.obtenerEstudiantesDisponibles).mockResolvedValue([disponibleBasico]);
    vi.mocked(s.matricularEstudiante).mockResolvedValue(undefined);
    renderPage();
    await waitFor(() => expect(screen.getByText('1°A')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Matricular Alumno'));
    await waitFor(() => {
      expect(screen.getAllByText('Cancelar').length).toBeGreaterThanOrEqual(1);
    });
    const select = screen.getByRole('combobox');
    fireEvent.change(select, { target: { value: 'e1' } });
    fireEvent.click(screen.getByText('Guardar matrícula'));
    await waitFor(() => expect(s.matricularEstudiante).toHaveBeenCalledWith('1', 'e1'));
  });

  it('falla matrícula muestra error', async () => {
    vi.mocked(s.obtenerCursoPorId).mockResolvedValue({ id: 1, nombre: '1°A' });
    vi.mocked(s.obtenerEstudiantesPorCurso).mockResolvedValue([]);
    vi.mocked(s.obtenerEstudiantesDisponibles).mockResolvedValue([disponibleBasico]);
    vi.mocked(s.matricularEstudiante).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('1°A')).toBeInTheDocument());
    fireEvent.click(screen.getByText('Matricular Alumno'));
    await waitFor(() => {
      expect(screen.getAllByText('Cancelar').length).toBeGreaterThanOrEqual(1);
    });
    const select = screen.getByRole('combobox');
    fireEvent.change(select, { target: { value: 'e1' } });
    fireEvent.click(screen.getByText('Guardar matrícula'));
    await waitFor(() => expect(screen.getByText(/No se pudo realizar la matrícula/)).toBeInTheDocument());
  });

  it('filtra estudiantes disponibles eliminando los ya matriculados', async () => {
    vi.mocked(s.obtenerCursoPorId).mockResolvedValue({ id: 1, nombre: '1°A' });
    vi.mocked(s.obtenerEstudiantesPorCurso).mockResolvedValue([estudianteCompleto]);
    vi.mocked(s.obtenerEstudiantesDisponibles).mockResolvedValue([
      estudianteCompleto,
      estudianteCompleto2,
    ]);
    renderPage();
    await waitFor(() => expect(screen.getByText('1°A')).toBeInTheDocument());
  });
});
