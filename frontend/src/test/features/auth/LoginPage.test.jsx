import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import LoginPage from '../../../features/auth/pages/LoginPage';
import { AuthProvider } from '../../../core/context/AuthContext.jsx';

vi.mock('../../../features/auth/services/authService');

const wrapper = ({ children }) => (
  <MemoryRouter>
    <AuthProvider>{children}</AuthProvider>
  </MemoryRouter>
);

describe('LoginPage', () => {
  it('renderiza el formulario de login', () => {
    render(<LoginPage />, { wrapper });
    expect(screen.getByText('Iniciar Sesión')).toBeInTheDocument();
  });

  it('renderiza la información institucional', () => {
    render(<LoginPage />, { wrapper });
    expect(screen.getByText('Libro de Clases Digital')).toBeInTheDocument();
  });

  it('renderiza el campo de RUT', () => {
    render(<LoginPage />, { wrapper });
    expect(screen.getByPlaceholderText('12345678-9')).toBeInTheDocument();
  });

  it('renderiza el campo de contraseña', () => {
    render(<LoginPage />, { wrapper });
    expect(screen.getByPlaceholderText('••••••••')).toBeInTheDocument();
  });

  it('renderiza el botón de Entrar', () => {
    render(<LoginPage />, { wrapper });
    expect(screen.getByRole('button', { name: 'Entrar' })).toBeInTheDocument();
  });
});
