import { describe, it, expect } from 'vitest';
import { render, screen } from '@testing-library/react';
import AuthLayout from '../../../features/auth/components/AuthLayout';

describe('AuthLayout', () => {
  it('renderiza el badge institucional', () => {
    render(<AuthLayout><div>contenido</div></AuthLayout>);
    expect(screen.getByText('Libro de Clases Digital')).toBeInTheDocument();
  });

  it('renderiza el título y subtítulo', () => {
    render(<AuthLayout><div>contenido</div></AuthLayout>);
    expect(screen.getByText('CBO — Sistema de Gestión Académica')).toBeInTheDocument();
    expect(screen.getByText('Gestiona tu aula de forma eficiente y moderna')).toBeInTheDocument();
  });

  it('renderiza la descripción', () => {
    render(<AuthLayout><div>contenido</div></AuthLayout>);
    expect(screen.getByText(/Centraliza el control de asistencia/)).toBeInTheDocument();
  });

  it('renderiza los children en la columna del formulario', () => {
    render(<AuthLayout><div data-testid="child">Formulario</div></AuthLayout>);
    expect(screen.getByTestId('child')).toHaveTextContent('Formulario');
  });

  it('tiene aria-labels en las secciones', () => {
    render(<AuthLayout><div>contenido</div></AuthLayout>);
    expect(screen.getByLabelText('Información institucional')).toBeInTheDocument();
    expect(screen.getByLabelText('Contenido del formulario')).toBeInTheDocument();
  });
});
