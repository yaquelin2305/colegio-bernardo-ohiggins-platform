import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import AuthLayout from '../../../features/auth/components/AuthLayout';

describe('AuthLayout', () => {
  it('renderiza children dentro del layout', () => {
    render(<AuthLayout><p>Contenido del formulario</p></AuthLayout>);
    expect(screen.getByText('Contenido del formulario')).toBeInTheDocument();
  });

  it('tiene aria-labels en las secciones', () => {
    render(<AuthLayout><div>test</div></AuthLayout>);
    expect(screen.getByLabelText('Información institucional')).toBeInTheDocument();
    expect(screen.getByLabelText('Contenido del formulario')).toBeInTheDocument();
  });

  it('muestra el título institucional', () => {
    render(<AuthLayout><div>test</div></AuthLayout>);
    expect(screen.getByText('CBO — Sistema de Gestión Académica')).toBeInTheDocument();
    expect(screen.getByText('Libro de Clases Digital')).toBeInTheDocument();
  });
});
