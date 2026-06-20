import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import TablaHistorialAsistencia from '../../../features/asistencia/components/TablaHistorialAsistencia';

const REGISTROS = [
  { id: 1, fecha: '2026-06-15', estado: 'presente', anotacion: null },
  { id: 2, fecha: '2026-06-16', estado: 'ausente', anotacion: 'Sin aviso' },
];

describe('TablaHistorialAsistencia', () => {
  it('muestra mensaje vacío cuando no hay registros', () => {
    render(<TablaHistorialAsistencia registros={[]} />);
    expect(screen.getByText('Sin registros de asistencia para este alumno.')).toBeInTheDocument();
  });

  it('renderiza registros con estado', () => {
    render(<TablaHistorialAsistencia registros={REGISTROS} />);
    expect(screen.getByText('Presente')).toBeInTheDocument();
    expect(screen.getByText('Ausente')).toBeInTheDocument();
    expect(screen.getByText('Sin aviso')).toBeInTheDocument();
  });
});
