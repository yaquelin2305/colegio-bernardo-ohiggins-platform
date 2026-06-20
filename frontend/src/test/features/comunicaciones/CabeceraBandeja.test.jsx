import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import CabeceraBandeja from '../../../features/comunicaciones/components/CabeceraBandeja';

describe('CabeceraBandeja', () => {
  it('muestra la cantidad de no leídos', () => {
    render(<CabeceraBandeja totalNoLeidos={5} onRedactar={vi.fn()} />);
    expect(screen.getByText(/5 mensajes sin leer/)).toBeInTheDocument();
  });

  it('muestra 0 no leídos', () => {
    render(<CabeceraBandeja totalNoLeidos={0} onRedactar={vi.fn()} />);
    expect(screen.getByText(/0 mensajes sin leer/)).toBeInTheDocument();
  });

  it('llama onRedactar al hacer click en redactar', () => {
    const onRedactar = vi.fn();
    render(<CabeceraBandeja onRedactar={onRedactar} />);
    fireEvent.click(screen.getByText('Redactar Nuevo'));
    expect(onRedactar).toHaveBeenCalledOnce();
  });
});
