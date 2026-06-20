import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import FiltroCursoAnotaciones from '../../../features/asistencia/components/FiltroCursoAnotaciones';

const CURSOS = [
  { id: 1, nombre: '1°A Básica' },
  { id: 2, nombre: '2°B Básica' },
];

describe('FiltroCursoAnotaciones', () => {
  it('renderiza select con cursos', () => {
    render(<FiltroCursoAnotaciones cursos={CURSOS} cursoId={1} onChange={vi.fn()} />);
    expect(screen.getByText('1°A Básica')).toBeInTheDocument();
    expect(screen.getByText('2°B Básica')).toBeInTheDocument();
  });

  it('llama onChange al cambiar curso', () => {
    const onChange = vi.fn();
    render(<FiltroCursoAnotaciones cursos={CURSOS} cursoId={1} onChange={onChange} />);
    fireEvent.change(screen.getByLabelText('Curso'), { target: { value: '2' } });
    expect(onChange).toHaveBeenCalled();
  });
});
