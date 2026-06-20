import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import NotFoundPage from '../../shared/components/NotFoundPage';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal();
  return { ...actual, useNavigate: () => mockNavigate };
});

const mockUseAuth = vi.fn();
vi.mock('../../core/context/useAuth', () => ({
  useAuth: () => mockUseAuth(),
}));

describe('NotFoundPage', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  it('navega a /login cuando no hay usuario', () => {
    mockUseAuth.mockReturnValue({ usuario: null, token: null });
    render(<MemoryRouter><NotFoundPage /></MemoryRouter>);
    expect(screen.getByText('404')).toBeInTheDocument();
    expect(screen.getByText('Página no encontrada')).toBeInTheDocument();
    fireEvent.click(screen.getByText('Volver al inicio'));
    expect(mockNavigate).toHaveBeenCalledWith('/login');
  });

  it('navega a /dashboard para rol ADMIN', () => {
    mockUseAuth.mockReturnValue({ usuario: { rol: 'ADMIN' }, token: 'x' });
    render(<MemoryRouter><NotFoundPage /></MemoryRouter>);
    fireEvent.click(screen.getByText('Volver al inicio'));
    expect(mockNavigate).toHaveBeenCalledWith('/dashboard');
  });

  it('navega a /mis-calificaciones para ESTUDIANTE', () => {
    mockUseAuth.mockReturnValue({ usuario: { rol: 'ESTUDIANTE' }, token: 'x' });
    render(<MemoryRouter><NotFoundPage /></MemoryRouter>);
    fireEvent.click(screen.getByText('Volver al inicio'));
    expect(mockNavigate).toHaveBeenCalledWith('/mis-calificaciones');
  });
});
