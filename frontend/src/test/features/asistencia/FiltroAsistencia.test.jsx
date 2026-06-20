import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import FiltroAsistencia from '../../../features/asistencia/components/FiltroAsistencia';

describe('FiltroAsistencia', () => {
  const CURSOS = [{ id: 1, nombre: '1°A' }, { id: 2, nombre: '2°B' }];

  it('renderiza formulario con select de cursos y fecha', () => {
    render(<FiltroAsistencia cursos={CURSOS} onFiltrar={vi.fn()} />);
    expect(screen.getByLabelText('Curso')).toBeInTheDocument();
    expect(screen.getByLabelText('Fecha')).toBeInTheDocument();
    expect(screen.getByText('Filtrar')).toBeInTheDocument();
  });

  it('llama onFiltrar con curso y fecha al submit', () => {
    const onFiltrar = vi.fn();
    render(<FiltroAsistencia cursos={CURSOS} onFiltrar={onFiltrar} />);
    fireEvent.change(screen.getByLabelText('Curso'), { target: { value: '1' } });
    fireEvent.click(screen.getByText('Filtrar'));
    expect(onFiltrar).toHaveBeenCalledWith({ curso: '1', fecha: expect.any(String) });
  });

  it('no llama onFiltrar si no hay función', () => {
    render(<FiltroAsistencia cursos={CURSOS} />);
    fireEvent.click(screen.getByText('Filtrar'));
  });
});
