import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import TablaInasistenciasJustificadas from '../../../features/asistencia/components/TablaInasistenciasJustificadas';

const JUSTIFICADAS = [
  { id: 1, fecha: '2026-06-10', alumno: 'Juan Pérez', curso: '1°A', estado: 'justificado' },
];

describe('TablaInasistenciasJustificadas', () => {
  it('retorna null cuando no hay justificadas', () => {
    const { container } = render(<TablaInasistenciasJustificadas justificadas={[]} />);
    expect(container.innerHTML).toBe('');
  });

  it('renderiza tabla con justificadas', () => {
    render(<TablaInasistenciasJustificadas justificadas={JUSTIFICADAS} />);
    expect(screen.getByText('Juan Pérez')).toBeInTheDocument();
    expect(screen.getByText('1°A')).toBeInTheDocument();
    expect(screen.getByText('Justificadas')).toBeInTheDocument();
  });
});
