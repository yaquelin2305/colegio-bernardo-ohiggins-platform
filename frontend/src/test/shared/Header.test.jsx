import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import Header from '../../shared/components/layout/Header';

const mockLogout = vi.hoisted(() => vi.fn());
vi.mock('../../core/context/useAuth', () => ({
  useAuth: () => ({
    usuario: { nombre: 'Juan Pérez', role: 'ADMIN' },
    token: 'fake-token',
    login: vi.fn(),
    logout: mockLogout,
  }),
}));

describe('Header', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  it('muestra el título y el nombre del usuario', () => {
    render(<Header titulo="Dashboard" />);
    expect(screen.getByText('Dashboard')).toBeInTheDocument();
    expect(screen.getByText('Juan Pérez')).toBeInTheDocument();
  });

  it('muestra iniciales del usuario', () => {
    render(<Header titulo="Dashboard" />);
    expect(screen.getByText('JP')).toBeInTheDocument();
  });

  it('llama logout al hacer click en cerrar sesión', () => {
    render(<Header titulo="Dashboard" />);
    fireEvent.click(screen.getByLabelText('Cerrar sesión'));
    expect(mockLogout).toHaveBeenCalledOnce();
  });

  it('muestra título por defecto cuando no se pasa', () => {
    render(<Header />);
    expect(screen.getByText('Dashboard')).toBeInTheDocument();
  });
});
