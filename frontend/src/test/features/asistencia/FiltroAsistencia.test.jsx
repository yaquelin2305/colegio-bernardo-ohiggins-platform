import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import FiltroAsistencia from '../../../features/asistencia/components/FiltroAsistencia';

const cursosBase = [
  { id: '1', nombre: '1°A Básica' },
  { id: '2', nombre: '2°B Básica' },
];

beforeEach(() => {
  vi.useFakeTimers();
  vi.setSystemTime(new Date('2026-06-09'));
});

afterEach(() => {
  vi.useRealTimers();
});

describe('FiltroAsistencia', () => {
  it('renderiza cursos recibidos por props', () => {
    render(<FiltroAsistencia cursos={cursosBase} onFiltrar={() => {}} />);
    expect(screen.getByText('1°A Básica')).toBeInTheDocument();
    expect(screen.getByText('2°B Básica')).toBeInTheDocument();
  });

  it('actualiza curso seleccionado', () => {
    render(<FiltroAsistencia cursos={cursosBase} onFiltrar={() => {}} />);
    fireEvent.change(screen.getByLabelText('Curso'), { target: { value: '1' } });
    expect(screen.getByDisplayValue('1°A Básica')).toBeInTheDocument();
  });

  it('actualiza fecha', () => {
    render(<FiltroAsistencia cursos={cursosBase} onFiltrar={() => {}} />);
    const input = screen.getByLabelText('Fecha');
    fireEvent.change(input, { target: { value: '2026-06-15' } });
    expect(screen.getByDisplayValue('2026-06-15')).toBeInTheDocument();
  });

  it('ejecuta onFiltrar enviando curso y fecha', () => {
    const mockOnFiltrar = vi.fn();
    render(<FiltroAsistencia cursos={cursosBase} onFiltrar={mockOnFiltrar} />);
    fireEvent.change(screen.getByLabelText('Curso'), { target: { value: '1' } });
    fireEvent.click(screen.getByRole('button', { name: /Filtrar/i }));
    expect(mockOnFiltrar).toHaveBeenCalledWith({ curso: '1', fecha: '2026-06-09' });
  });
});
