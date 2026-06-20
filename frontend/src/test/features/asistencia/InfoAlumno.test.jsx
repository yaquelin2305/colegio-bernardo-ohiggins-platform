import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import InfoAlumno from '../../../features/asistencia/components/InfoAlumno';

const ALUMNO = { nombre: 'Juan Pérez', rut: '12345678-9', curso: '1°A' };

describe('InfoAlumno', () => {
  it('muestra datos del alumno', () => {
    render(<InfoAlumno alumno={ALUMNO} porcentaje={85} />);
    expect(screen.getByText('Juan Pérez')).toBeInTheDocument();
    expect(screen.getByText('12345678-9')).toBeInTheDocument();
    expect(screen.getByText('1°A')).toBeInTheDocument();
  });

  it('muestra porcentaje de asistencia cuando se pasa', () => {
    render(<InfoAlumno alumno={ALUMNO} porcentaje={75} />);
    expect(screen.getByText('75%')).toBeInTheDocument();
  });

  it('no muestra porcentaje cuando es null', () => {
    render(<InfoAlumno alumno={ALUMNO} porcentaje={null} />);
    expect(screen.queryByText('% Asistencia')).not.toBeInTheDocument();
  });

  it('aplica clase crítica cuando porcentaje < 75', () => {
    render(<InfoAlumno alumno={ALUMNO} porcentaje={50} />);
    expect(screen.getByText('50%')).toHaveClass('historial__porcentaje--critico');
  });

  it('aplica clase ok cuando porcentaje >= 75', () => {
    render(<InfoAlumno alumno={ALUMNO} porcentaje={85} />);
    expect(screen.getByText('85%')).toHaveClass('historial__porcentaje--ok');
  });
});
