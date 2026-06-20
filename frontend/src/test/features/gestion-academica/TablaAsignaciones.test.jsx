import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import TablaAsignaciones from '../../../features/gestion-academica/components/TablaAsignaciones';

const ASIGNACIONES = [
  { id: 1, docente: { nombre: 'Prof. Pérez' }, curso: { nombre: '1°A' }, asignatura: { nombre: 'Matemáticas' } },
  { id: 2, docente: { nombre: 'Prof. López' }, curso: { nombre: '2°B' }, asignatura: { nombre: 'Lenguaje' } },
];

describe('TablaAsignaciones', () => {
  it('renderiza tabla con asignaciones', () => {
    render(<TablaAsignaciones asignaciones={ASIGNACIONES} onEliminar={vi.fn()} />);
    expect(screen.getByText('Prof. Pérez')).toBeInTheDocument();
    expect(screen.getByText('Prof. López')).toBeInTheDocument();
    expect(screen.getByText('1°A')).toBeInTheDocument();
  });

  it('llama onEliminar al hacer click en eliminar', () => {
    const onEliminar = vi.fn();
    render(<TablaAsignaciones asignaciones={ASIGNACIONES} onEliminar={onEliminar} />);
    const btns = screen.getAllByLabelText(/Eliminar asignación/);
    fireEvent.click(btns[0]);
    expect(onEliminar).toHaveBeenCalledWith(1);
  });
});
