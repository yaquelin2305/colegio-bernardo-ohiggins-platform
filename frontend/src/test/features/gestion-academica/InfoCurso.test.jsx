import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import InfoCurso from '../../../features/gestion-academica/components/InfoCurso';

const CURSO = { nombre: '1°A Básica', nivel: 'Básica', curso: '1°A' };

describe('InfoCurso', () => {
  it('muestra información del curso', () => {
    render(<InfoCurso curso={CURSO} />);
    expect(screen.getByText('1°A Básica')).toBeInTheDocument();
  });

  it('muestra botón matricular alumno', () => {
    render(<InfoCurso curso={CURSO} />);
    expect(screen.getByText('Matricular Alumno')).toBeInTheDocument();
  });
});
