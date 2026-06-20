import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import EncabezadoBoletin from '../../../features/gestion-academica/components/EncabezadoBoletin';

const ALUMNO = {
  nombre: 'Juan Pérez',
  rut: '12345678-9',
  curso: '1°A Básica',
  periodo: '2026',
  promedioGeneral: 5.8,
  asistencia: 92,
};

describe('EncabezadoBoletin', () => {
  it('muestra datos del alumno', () => {
    render(<EncabezadoBoletin alumno={ALUMNO} />);
    expect(screen.getByText('Juan Pérez')).toBeInTheDocument();
    expect(screen.getByText('12345678-9')).toBeInTheDocument();
    expect(screen.getByText('1°A Básica')).toBeInTheDocument();
    expect(screen.getByText('2026')).toBeInTheDocument();
  });

  it('muestra promedio general y asistencia', () => {
    render(<EncabezadoBoletin alumno={ALUMNO} />);
    expect(screen.getByText('5.8')).toBeInTheDocument();
    expect(screen.getByText('92%')).toBeInTheDocument();
    expect(screen.getByLabelText('Promedio general')).toBeInTheDocument();
    expect(screen.getByLabelText('Porcentaje de asistencia')).toBeInTheDocument();
  });
});
