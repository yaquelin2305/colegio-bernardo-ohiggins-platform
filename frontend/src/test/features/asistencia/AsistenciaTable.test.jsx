import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import AsistenciaTable from '../../../features/asistencia/components/AsistenciaTable';

const estudiantes = [
  { id: 'uuid-1', nombre: 'Juan Pérez', curso: '1°A', estado: 'presente', hora: '09:00' },
  { id: 'uuid-2', nombre: 'María López', curso: '1°A', estado: 'ausente', hora: '--' },
  { id: 'uuid-3', nombre: 'Pedro Díaz', curso: '1°A', estado: 'justificado', hora: '--' },
];

describe('AsistenciaTable', () => {
  it('muestra mensaje vacío cuando no hay estudiantes', () => {
    render(<AsistenciaTable estudiantes={[]} />);
    expect(screen.getByText('No hay registros de asistencia para los filtros seleccionados.')).toBeInTheDocument();
  });

  it('renderiza los nombres de los estudiantes', () => {
    render(<AsistenciaTable estudiantes={estudiantes} />);
    expect(screen.getByText('Juan Pérez')).toBeInTheDocument();
    expect(screen.getByText('María López')).toBeInTheDocument();
    expect(screen.getByText('Pedro Díaz')).toBeInTheDocument();
  });

  it('muestra el curso de cada estudiante', () => {
    render(<AsistenciaTable estudiantes={estudiantes} />);
    const spans = screen.getAllByText('1°A');
    expect(spans.length).toBe(3);
  });

  it('muestra los selects de estado con valor correcto', () => {
    render(<AsistenciaTable estudiantes={estudiantes} />);
    const selects = screen.getAllByRole('combobox');
    expect(selects[0]).toHaveValue('presente');
    expect(selects[1]).toHaveValue('ausente');
    expect(selects[2]).toHaveValue('justificado');
  });

  it('muestra la hora de cada estudiante', () => {
    render(<AsistenciaTable estudiantes={estudiantes} />);
    expect(screen.getByText('09:00')).toBeInTheDocument();
  });

  it('llama onCambiarEstado al cambiar el select', () => {
    const onCambiarEstado = vi.fn();
    render(<AsistenciaTable estudiantes={[estudiantes[0]]} onCambiarEstado={onCambiarEstado} />);
    const select = screen.getByRole('combobox');
    fireEvent.change(select, { target: { value: 'ausente' } });
    expect(onCambiarEstado).toHaveBeenCalledWith('uuid-1', 'ausente');
  });

  it('tiene aria-label en la tabla', () => {
    render(<AsistenciaTable estudiantes={estudiantes} />);
    expect(screen.getByRole('table', { name: 'Lista de asistencia' })).toBeInTheDocument();
  });

  it('muestra los números de fila', () => {
    render(<AsistenciaTable estudiantes={estudiantes} />);
    const nums = screen.getAllByText(/^[0-9]+$/);
    expect(nums.length).toBe(3);
    expect(nums[0]).toHaveTextContent('1');
    expect(nums[1]).toHaveTextContent('2');
    expect(nums[2]).toHaveTextContent('3');
  });
});
