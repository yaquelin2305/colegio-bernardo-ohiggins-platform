import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import FiltroAlumno from '../../../features/asistencia/components/FiltroAlumno';

const ALUMNOS = [
  { id: '1', nombre: 'Juan Pérez', curso: '1°A' },
  { id: '2', nombre: 'María López', curso: '2°B' },
];

describe('FiltroAlumno', () => {
  it('renderiza el select con opciones', () => {
    render(<FiltroAlumno alumnos={ALUMNOS} alumnoId="" onChange={vi.fn()} />);
    expect(screen.getByText('— Selecciona un alumno —')).toBeInTheDocument();
    expect(screen.getByText('Juan Pérez (1°A)')).toBeInTheDocument();
    expect(screen.getByText('María López (2°B)')).toBeInTheDocument();
  });

  it('llama onChange al seleccionar un alumno', () => {
    const onChange = vi.fn();
    render(<FiltroAlumno alumnos={ALUMNOS} alumnoId="" onChange={onChange} />);
    fireEvent.change(screen.getByLabelText('Alumno'), { target: { value: '2' } });
    expect(onChange).toHaveBeenCalled();
  });
});
