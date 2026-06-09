import { describe, it, expect, vi } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import AsistenciaTable from '../../../features/asistencia/components/AsistenciaTable';

const estudiantesBase = [
  { id: 1, nombre: 'Ana García', curso: '1°A', estado: 'presente', hora: '09:00' },
  { id: 2, nombre: 'Luis Pérez', curso: '1°B', estado: 'ausente', hora: '09:05' },
];

describe('AsistenciaTable', () => {
  it('muestra mensaje vacío cuando no existen estudiantes', () => {
    render(<AsistenciaTable estudiantes={[]} onCambiarEstado={() => {}} />);
    expect(screen.getByText('No hay registros de asistencia para los filtros seleccionados.')).toBeInTheDocument();
  });

  it('renderiza correctamente los estudiantes recibidos', () => {
    render(<AsistenciaTable estudiantes={estudiantesBase} onCambiarEstado={() => {}} />);
    expect(screen.getByText('Ana García')).toBeInTheDocument();
    expect(screen.getByText('1°A')).toBeInTheDocument();
    expect(screen.getByText('09:00')).toBeInTheDocument();
    expect(screen.getByRole('table', { name: 'Lista de asistencia' })).toBeInTheDocument();
  });

  it('ejecuta onCambiarEstado al modificar el select', () => {
    const mockFn = vi.fn();
    render(<AsistenciaTable estudiantes={estudiantesBase} onCambiarEstado={mockFn} />);
    fireEvent.change(screen.getByLabelText('Estado de asistencia de Ana García'), { target: { value: 'ausente' } });
    expect(mockFn).toHaveBeenCalledWith(1, 'ausente');
  });

  it('renderiza los tres estados disponibles', () => {
    render(<AsistenciaTable estudiantes={[estudiantesBase[0]]} onCambiarEstado={() => {}} />);
    expect(screen.getByText('Presente')).toBeInTheDocument();
    expect(screen.getByText('Ausente')).toBeInTheDocument();
    expect(screen.getByText('Justificado')).toBeInTheDocument();
  });
});
