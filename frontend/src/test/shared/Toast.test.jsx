import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import Toast from '../../shared/components/ui/Toast';

describe('Toast', () => {
  it('retorna null cuando toast es null', () => {
    const { container } = render(<Toast toast={null} onClose={vi.fn()} />);
    expect(container.innerHTML).toBe('');
  });

  it('muestra mensaje de éxito con icono CheckCircle', () => {
    render(<Toast toast={{ type: 'success', message: 'Guardado correctamente' }} onClose={vi.fn()} />);
    expect(screen.getByText('Guardado correctamente')).toBeInTheDocument();
    expect(screen.getByRole('alert')).toHaveClass('toast--success');
  });

  it('muestra mensaje de error con icono XCircle', () => {
    render(<Toast toast={{ type: 'error', message: 'Error al guardar' }} onClose={vi.fn()} />);
    expect(screen.getByText('Error al guardar')).toBeInTheDocument();
    expect(screen.getByRole('alert')).toHaveClass('toast--error');
  });

  it('llama onClose al hacer click en cerrar', () => {
    const onClose = vi.fn();
    render(<Toast toast={{ type: 'success', message: 'Ok' }} onClose={onClose} />);
    fireEvent.click(screen.getByLabelText('Cerrar notificación'));
    expect(onClose).toHaveBeenCalledOnce();
  });
});
