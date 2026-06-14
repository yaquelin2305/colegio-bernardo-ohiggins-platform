import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import AsistenciaTable from '../../../features/asistencia/components/AsistenciaTable';

const estudiantesBase = [
  { id: 1, nombre: 'Ana García', curso: '1°A', estado: 'presente', hora: '09:00' },
  { id: 2, nombre: 'Luis Pérez', curso: '1°B', estado: 'ausente', hora: '09:05' },
];

describe('AsistenciaTable', () => {
  it('ejecuta onCambiarEstado al modificar el select', () => {
    const mockFn = vi.fn();
    render(<AsistenciaTable estudiantes={estudiantesBase} onCambiarEstado={mockFn} />);
    fireEvent.change(screen.getByLabelText('Estado de asistencia de Ana García'), { target: { value: 'ausente' } });
    expect(mockFn).toHaveBeenCalledWith(1, 'ausente');
  });
});
