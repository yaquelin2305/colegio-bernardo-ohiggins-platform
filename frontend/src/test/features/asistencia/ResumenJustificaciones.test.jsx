import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import ResumenJustificaciones from '../../../features/asistencia/components/ResumenJustificaciones';

describe('ResumenJustificaciones', () => {
  it('muestra totales pendientes y justificadas', () => {
    render(<ResumenJustificaciones totalPendientes={3} totalJustificadas={10} />);
    expect(screen.getByText('3')).toBeInTheDocument();
    expect(screen.getByText('10')).toBeInTheDocument();
    expect(screen.getByText('Pendientes')).toBeInTheDocument();
    expect(screen.getByText('Justificadas')).toBeInTheDocument();
  });

  it('muestra ceros cuando no hay valores', () => {
    render(<ResumenJustificaciones totalPendientes={0} totalJustificadas={0} />);
    const ceros = screen.getAllByText('0');
    expect(ceros.length).toBeGreaterThanOrEqual(2);
  });
});
