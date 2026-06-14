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
  it('ejecuta onFiltrar enviando curso y fecha', () => {
    const mockOnFiltrar = vi.fn();
    render(<FiltroAsistencia cursos={cursosBase} onFiltrar={mockOnFiltrar} />);
    fireEvent.change(screen.getByLabelText('Curso'), { target: { value: '1' } });
    fireEvent.click(screen.getByRole('button', { name: /Filtrar/i }));
    expect(mockOnFiltrar).toHaveBeenCalledWith({ curso: '1', fecha: '2026-06-09' });
  });
});
