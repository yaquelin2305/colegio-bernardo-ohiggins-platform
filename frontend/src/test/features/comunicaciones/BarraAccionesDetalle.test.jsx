import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import BarraAccionesDetalle from '../../../features/comunicaciones/components/BarraAccionesDetalle';

describe('BarraAccionesDetalle', () => {
  it('llama onVolver al hacer click en volver', () => {
    const onVolver = vi.fn();
    render(<BarraAccionesDetalle onVolver={onVolver} />);
    fireEvent.click(screen.getByText('Volver'));
    expect(onVolver).toHaveBeenCalledOnce();
  });

  it('llama onResponder al hacer click en responder', () => {
    const onResponder = vi.fn();
    render(<BarraAccionesDetalle onResponder={onResponder} />);
    fireEvent.click(screen.getByText('Responder'));
    expect(onResponder).toHaveBeenCalledOnce();
  });
});
