import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import TablaEstudiantes from '../../../features/gestion-academica/components/TablaEstudiantes';

const ESTUDIANTES = [
  { id: '1', rut: '12345678-9', nombre: 'Juan', apellido: 'Pérez', email: 'juan@test.cl', promedio: 5.5, asistencia: 90 },
  { id: '2', rut: '98765432-1', nombre: 'María', apellido: 'López', email: 'maria@test.cl', promedio: 4.0, asistencia: 75 },
];

describe('TablaEstudiantes', () => {
  it('renderiza lista de estudiantes', () => {
    render(<TablaEstudiantes estudiantes={ESTUDIANTES} />);
    expect(screen.getByText('12345678-9')).toBeInTheDocument();
    expect(screen.getByText('98765432-1')).toBeInTheDocument();
    expect(screen.getByText('Juan')).toBeInTheDocument();
    expect(screen.getByText('Pérez')).toBeInTheDocument();
    expect(screen.getByText('María')).toBeInTheDocument();
    expect(screen.getByText('López')).toBeInTheDocument();
  });

  it('muestra promedio y asistencia', () => {
    render(<TablaEstudiantes estudiantes={ESTUDIANTES} />);
    expect(screen.getByText('5.5')).toBeInTheDocument();
    expect(screen.getByText('90%')).toBeInTheDocument();
  });
});
