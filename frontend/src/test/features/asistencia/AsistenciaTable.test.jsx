import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import AsistenciaTable from '../../../features/asistencia/components/AsistenciaTable';

const ESTUDIANTES = [
  { id: 1, nombre: 'Juan Pérez', curso: '1°A', estado: 'presente', hora: '10:30' },
  { id: 2, nombre: 'María López', curso: '2°B', estado: 'ausente', hora: null },
];

describe('AsistenciaTable', () => {
  it('muestra mensaje vacío cuando no hay estudiantes', () => {
    render(<AsistenciaTable estudiantes={[]} onCambiarEstado={vi.fn()} />);
    expect(screen.getByText('No hay registros de asistencia para los filtros seleccionados.')).toBeInTheDocument();
  });

  it('renderiza estudiantes en la tabla', () => {
    render(<AsistenciaTable estudiantes={ESTUDIANTES} onCambiarEstado={vi.fn()} />);
    expect(screen.getByText('Juan Pérez')).toBeInTheDocument();
    expect(screen.getByText('María López')).toBeInTheDocument();
    expect(screen.getByText('1°A')).toBeInTheDocument();
    expect(screen.getByText('2°B')).toBeInTheDocument();
  });

  it('llama onCambiarEstado al cambiar el select', () => {
    const onCambiarEstado = vi.fn();
    render(<AsistenciaTable estudiantes={ESTUDIANTES} onCambiarEstado={onCambiarEstado} />);
    const selects = screen.getAllByRole('combobox');
    fireEvent.change(selects[0], { target: { value: 'ausente' } });
    expect(onCambiarEstado).toHaveBeenCalledWith(1, 'ausente');
  });
});
