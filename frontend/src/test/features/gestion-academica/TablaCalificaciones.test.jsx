import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import TablaCalificaciones from '../../../features/gestion-academica/components/TablaCalificaciones';

const ALUMNOS = [
  { id: '1', nombre: 'Juan Pérez', nota1: 5.0, nota2: 6.0, nota3: 4.0, promedio: 5.0 },
  { id: '2', nombre: 'María López', nota1: 3.0, nota2: 4.0, nota3: 5.0, promedio: 4.0 },
];

describe('TablaCalificaciones', () => {
  it('renderiza tabla con alumnos y notas editables', () => {
    render(<TablaCalificaciones alumnos={ALUMNOS} onNotaChange={vi.fn()} />);
    expect(screen.getByText('Juan Pérez')).toBeInTheDocument();
    expect(screen.getByText('María López')).toBeInTheDocument();
  });

  it('llama onNotaChange al editar un input', () => {
    const onNotaChange = vi.fn();
    render(<TablaCalificaciones alumnos={ALUMNOS} onNotaChange={onNotaChange} />);
    const inputs = screen.getAllByRole('spinbutton');
    fireEvent.change(inputs[0], { target: { value: '6.0' } });
    expect(onNotaChange).toHaveBeenCalledWith('1', 'nota1', '6.0');
  });
});
