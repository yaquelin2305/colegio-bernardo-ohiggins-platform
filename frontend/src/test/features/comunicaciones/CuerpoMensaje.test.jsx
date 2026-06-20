import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import CuerpoMensaje from '../../../features/comunicaciones/components/CuerpoMensaje';

describe('CuerpoMensaje', () => {
  it('renderiza el cuerpo del mensaje en un pre', () => {
    render(<CuerpoMensaje cuerpo="Hola, esto es un mensaje de prueba" />);
    const pre = screen.getByText('Hola, esto es un mensaje de prueba');
    expect(pre.tagName).toBe('PRE');
  });
});
