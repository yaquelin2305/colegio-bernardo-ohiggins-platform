import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import ResumenAsistencia from '../../../features/asistencia/components/ResumenAsistencia';

const resumenBase = {
  total: 30,
  presentes: 25,
  ausentes: 5,
  totalJustificados: 2,
  porcentaje: 83,
};

describe('ResumenAsistencia', () => {
  it('muestra todos los valores del resumen', () => {
    render(<ResumenAsistencia resumen={resumenBase} />);
    expect(screen.getByText('30')).toBeInTheDocument();
    expect(screen.getByText('25')).toBeInTheDocument();
    expect(screen.getByText('5')).toBeInTheDocument();
    expect(screen.getByText('2')).toBeInTheDocument();
    expect(screen.getByText('83%')).toBeInTheDocument();
  });
});
