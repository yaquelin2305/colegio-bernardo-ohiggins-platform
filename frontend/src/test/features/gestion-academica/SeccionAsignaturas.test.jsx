import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import SeccionAsignaturas from '../../../features/gestion-academica/components/SeccionAsignaturas';

const ASIGNATURAS = [
  { id: 1, nombre: 'Matemáticas', horasSemanales: 4 },
  { id: 2, nombre: 'Lenguaje', horasSemanales: 3 },
];
const FORMULARIO = { nombre: '', horasSemanales: '' };

describe('SeccionAsignaturas', () => {
  it('renderiza tabla con asignaturas', () => {
    render(<SeccionAsignaturas asignaturas={ASIGNATURAS} formulario={FORMULARIO} onChange={vi.fn()} onSubmit={vi.fn()} />);
    expect(screen.getByText('Matemáticas')).toBeInTheDocument();
    expect(screen.getByText('Lenguaje')).toBeInTheDocument();
  });

  it('renderiza formulario de creación', () => {
    render(<SeccionAsignaturas asignaturas={ASIGNATURAS} formulario={FORMULARIO} onChange={vi.fn()} onSubmit={vi.fn()} />);
    expect(screen.getByText('Agregar Asignatura')).toBeInTheDocument();
  });
});
