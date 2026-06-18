import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import ResumenAsistencia from '../../../features/asistencia/components/ResumenAsistencia';

const resumenBase = {
  total: 30,
  presentes: 25,
  ausentes: 5,
  totalJustificados: 2,
  porcentaje: 83,
};

describe('ResumenAsistencia', () => {
  it('muestra todos los valores del resumen', () => {
    render(<ResumenAsistencia resumen={resumenBase} />);
    expect(screen.getByText('30')).toBeInTheDocument();
    expect(screen.getByText('25')).toBeInTheDocument();
    expect(screen.getByText('5')).toBeInTheDocument();
    expect(screen.getByText('2')).toBeInTheDocument();
    expect(screen.getByText('83%')).toBeInTheDocument();
  });

  it('muestra ceros cuando no se pasa prop resumen', () => {
    render(<ResumenAsistencia />);
    expect(screen.getByText('0%')).toBeInTheDocument();
    const ceros = screen.getAllByText('0');
    expect(ceros.length).toBeGreaterThan(0);
  });

  it('muestra ceros cuando resumen es null', () => {
    render(<ResumenAsistencia resumen={null} />);
    expect(screen.getByText('0%')).toBeInTheDocument();
  });

  it('muestra nombreCurso si está presente en el resumen', () => {
    render(<ResumenAsistencia resumen={{ ...resumenBase, nombreCurso: '1\u00B0A B\u00E1sica' }} />);
    expect(screen.getByText('1\u00B0A B\u00E1sica')).toBeInTheDocument();
  });

  it('no muestra p\u00E1rrafo de curso si no est\u00E1 en el resumen', () => {
    const { container } = render(<ResumenAsistencia resumen={resumenBase} />);
    expect(container.querySelector('.resumen-asistencia__curso')).not.toBeInTheDocument();
  });

  it('tiene aria-label de secci\u00F3n', () => {
    render(<ResumenAsistencia resumen={resumenBase} />);
    expect(screen.getByRole('region', { name: 'Resumen de asistencia' })).toBeInTheDocument();
  });

  it('muestra las 5 tarjetas de m\u00E9tricas', () => {
    const { container } = render(<ResumenAsistencia resumen={resumenBase} />);
    expect(container.querySelectorAll('.resumen-asistencia__card').length).toBe(5);
  });
});
