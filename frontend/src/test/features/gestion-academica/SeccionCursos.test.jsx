import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import SeccionCursos from '../../../features/gestion-academica/components/SeccionCursos';

const CURSOS = [
  { id: 1, nombre: '1°A Básica', anioEscolar: 2026 },
  { id: 2, nombre: '2°B Básica', anioEscolar: 2026 },
];
const FORMULARIO = { nombre: '', anioEscolar: '' };

describe('SeccionCursos', () => {
  it('renderiza tabla con cursos', () => {
    render(<MemoryRouter><SeccionCursos cursos={CURSOS} formulario={FORMULARIO} onChange={vi.fn()} onSubmit={vi.fn()} /></MemoryRouter>);
    expect(screen.getByText('1°A Básica')).toBeInTheDocument();
    expect(screen.getByText('2°B Básica')).toBeInTheDocument();
  });

  it('renderiza formulario de creación', () => {
    render(<MemoryRouter><SeccionCursos cursos={CURSOS} formulario={FORMULARIO} onChange={vi.fn()} onSubmit={vi.fn()} /></MemoryRouter>);
    expect(screen.getByText('Crear Curso')).toBeInTheDocument();
  });
});
