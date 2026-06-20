import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import TablaInasistenciasPendientes from '../../../features/asistencia/components/TablaInasistenciasPendientes';

const PENDIENTES = [
  { id: 1, fecha: '2026-06-10', alumno: 'Juan Pérez', curso: '1°A' },
  { id: 2, fecha: '2026-06-11', alumno: 'María López', curso: '2°B' },
];

describe('TablaInasistenciasPendientes', () => {
  it('renderiza inasistencias pendientes', () => {
    render(<TablaInasistenciasPendientes pendientes={PENDIENTES} formularioActivo={null} formulario={{ motivo: '' }} onAbrir={vi.fn()} onCerrar={vi.fn()} onChange={vi.fn()} onJustificar={vi.fn()} />);
    expect(screen.getByText('Juan Pérez')).toBeInTheDocument();
    expect(screen.getByText('María López')).toBeInTheDocument();
    expect(screen.getByText('1°A')).toBeInTheDocument();
  });

  it('abre formulario inline cuando formularioActivo coincide', () => {
    render(<TablaInasistenciasPendientes pendientes={PENDIENTES} formularioActivo={1} formulario={{ motivo: '' }} onAbrir={vi.fn()} onCerrar={vi.fn()} onChange={vi.fn()} onJustificar={vi.fn()} />);
    expect(screen.getByPlaceholderText('Describe el motivo de la inasistencia...')).toBeInTheDocument();
  });
});
