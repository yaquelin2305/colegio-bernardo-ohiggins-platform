import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import FiltroAsistencia from '../../../features/asistencia/components/FiltroAsistencia';

const cursos = [
  { id: 1, nombre: '1°A Básica' },
  { id: 2, nombre: '2°B Básica' },
];

describe('FiltroAsistencia', () => {
  it('renderiza el título', () => {
    render(<FiltroAsistencia />);
    expect(screen.getByText('Filtrar Asistencia')).toBeInTheDocument();
  });

  it('renderiza la lista de cursos en el select', () => {
    render(<FiltroAsistencia cursos={cursos} />);
    const select = screen.getByLabelText('Curso');
    expect(select).toBeInTheDocument();
    expect(screen.getByText('1°A Básica')).toBeInTheDocument();
    expect(screen.getByText('2°B Básica')).toBeInTheDocument();
  });

  it('renderiza el input de fecha con valor por defecto', () => {
    render(<FiltroAsistencia />);
    const fechaInput = screen.getByLabelText('Fecha');
    expect(fechaInput).toBeInTheDocument();
    expect(fechaInput).toHaveValue(new Date().toISOString().split('T')[0]);
  });

  it('renderiza el botón de filtrar', () => {
    render(<FiltroAsistencia />);
    expect(screen.getByRole('button', { name: 'Filtrar' })).toBeInTheDocument();
  });

  it('llama onFiltrar con curso y fecha al submitear', () => {
    const onFiltrar = vi.fn();
    render(<FiltroAsistencia cursos={cursos} onFiltrar={onFiltrar} />);
    const select = screen.getByLabelText('Curso');
    fireEvent.change(select, { target: { value: '1' } });
    fireEvent.click(screen.getByRole('button', { name: 'Filtrar' }));
    expect(onFiltrar).toHaveBeenCalledWith({ curso: '1', fecha: new Date().toISOString().split('T')[0] });
  });

  it('tiene aria-label en la sección', () => {
    render(<FiltroAsistencia />);
    expect(screen.getByLabelText('Filtros de asistencia')).toBeInTheDocument();
  });
});
