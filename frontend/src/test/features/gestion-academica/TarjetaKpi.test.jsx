import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import TarjetaKpi from '../../../features/gestion-academica/components/TarjetaKpi';

describe('TarjetaKpi', () => {
  it('muestra label, número y detalle', () => {
    render(<TarjetaKpi label="Estudiantes" numero={42} detalle="activos" variante="primary" />);
    expect(screen.getByText('Estudiantes')).toBeInTheDocument();
    expect(screen.getByText('42')).toBeInTheDocument();
    expect(screen.getByText('activos')).toBeInTheDocument();
  });

  it('no aplica clase modificadora cuando variante es primary', () => {
    const { container } = render(<TarjetaKpi label="Test" numero={1} variante="primary" />);
    expect(container.querySelector('.stat-card')).toBeInTheDocument();
    expect(container.querySelector('.stat-card--primary')).not.toBeInTheDocument();
  });

  it('aplica clase modificadora cuando variante no es primary', () => {
    const { container } = render(<TarjetaKpi label="Test" numero={1} variante="accent" />);
    expect(container.querySelector('.stat-card--accent')).toBeInTheDocument();
  });

  it('muestra el icono cuando se pasa como prop', () => {
    render(
      <TarjetaKpi label="Test" numero={1} variante="primary" icono={<span data-testid="icono-test">★</span>} />
    );
    expect(screen.getByTestId('icono-test')).toBeInTheDocument();
  });

  it('usa article como elemento semántico', () => {
    const { container } = render(<TarjetaKpi label="Test" numero={0} variante="primary" />);
    expect(container.querySelector('article')).toBeInTheDocument();
  });
});
