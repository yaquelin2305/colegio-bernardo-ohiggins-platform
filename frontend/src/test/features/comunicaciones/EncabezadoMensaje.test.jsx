import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import EncabezadoMensaje from '../../../features/comunicaciones/components/EncabezadoMensaje';

const MENSAJE = {
  asunto: 'Reunión de apoderados',
  remitente: 'Colegio B.O.',
  tipo: 'comunicado',
  canal: 'email',
  fecha: '2026-06-15T10:00:00.000Z',
};

describe('EncabezadoMensaje', () => {
  it('muestra asunto, remitente, tipo y canal', () => {
    render(<EncabezadoMensaje mensaje={MENSAJE} />);
    expect(screen.getByText('Reunión de apoderados')).toBeInTheDocument();
    expect(screen.getByText('Colegio B.O.')).toBeInTheDocument();
    expect(screen.getByText('comunicado')).toBeInTheDocument();
    expect(screen.getByText('email')).toBeInTheDocument();
  });

  it('muestra fecha formateada en locale chileno', () => {
    render(<EncabezadoMensaje mensaje={MENSAJE} />);
    expect(screen.getByText(/jun/i)).toBeInTheDocument();
  });
});
