import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import FiltrosRegistroNotas from '../../../features/gestion-academica/components/FiltrosRegistroNotas';

describe('FiltrosRegistroNotas', () => {
  const CURSOS = [{ id: 1, nombre: '1°A' }];
  const ASIGNATURAS = [{ id: 10, nombre: 'Matemáticas' }];

  it('renderiza selects de curso y asignatura', () => {
    render(<FiltrosRegistroNotas curso={1} asignatura={10} cursos={CURSOS} asignaturas={ASIGNATURAS} onCursoChange={vi.fn()} onAsignaturaChange={vi.fn()} />);
    expect(screen.getByText('1°A')).toBeInTheDocument();
    expect(screen.getByText('Matemáticas')).toBeInTheDocument();
  });

  it('llama onCursoChange al cambiar curso', () => {
    const onCursoChange = vi.fn();
    render(<FiltrosRegistroNotas curso={1} asignatura={10} cursos={CURSOS} asignaturas={ASIGNATURAS} onCursoChange={onCursoChange} onAsignaturaChange={vi.fn()} />);
    fireEvent.change(screen.getByLabelText('Curso'), { target: { value: '1' } });
    expect(onCursoChange).toHaveBeenCalled();
  });

  it('llama onAsignaturaChange al cambiar asignatura', () => {
    const onAsignaturaChange = vi.fn();
    render(<FiltrosRegistroNotas curso={1} asignatura={10} cursos={CURSOS} asignaturas={ASIGNATURAS} onCursoChange={vi.fn()} onAsignaturaChange={onAsignaturaChange} />);
    fireEvent.change(screen.getByLabelText('Asignatura'), { target: { value: '10' } });
    expect(onAsignaturaChange).toHaveBeenCalled();
  });
});
