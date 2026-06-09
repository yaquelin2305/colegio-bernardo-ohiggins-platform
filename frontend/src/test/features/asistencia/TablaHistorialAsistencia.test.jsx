import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import TablaHistorialAsistencia from '../../../features/asistencia/components/TablaHistorialAsistencia';

const registrosBase = [
  { id: 1, fecha: '2024-06-15', estado: 'presente', anotacion: 'Nota: buena conducta' },
  { id: 2, fecha: '2024-06-16', estado: 'ausente', anotacion: null },
];

describe('TablaHistorialAsistencia', () => {
  it('muestra mensaje cuando no existen registros', () => {
    render(<TablaHistorialAsistencia registros={[]} />);
    expect(screen.getByText('Sin registros de asistencia para este alumno.')).toBeInTheDocument();
  });

  it('renderiza registros recibidos', () => {
    const { container } = render(<TablaHistorialAsistencia registros={registrosBase} />);
    expect(screen.getByRole('region', { name: 'Registros de asistencia' })).toBeInTheDocument();
    expect(container.querySelector('.historial__celda-fecha')).toBeInTheDocument();
    expect(screen.getByText('Presente')).toBeInTheDocument();
    expect(screen.getByText('Nota: buena conducta')).toBeInTheDocument();
  });

  it('muestra estado Presente', () => {
    const { container } = render(<TablaHistorialAsistencia registros={[registrosBase[0]]} />);
    expect(container.querySelector('.historial__badge--presente')).toBeInTheDocument();
  });

  it('muestra estado Ausente', () => {
    const { container } = render(<TablaHistorialAsistencia registros={[registrosBase[1]]} />);
    expect(container.querySelector('.historial__badge--ausente')).toBeInTheDocument();
  });

  it('muestra guion cuando no existe anotación', () => {
    const { container } = render(<TablaHistorialAsistencia registros={[registrosBase[1]]} />);
    expect(container.querySelector('.historial__sin-anotacion')).toBeInTheDocument();
  });
});
