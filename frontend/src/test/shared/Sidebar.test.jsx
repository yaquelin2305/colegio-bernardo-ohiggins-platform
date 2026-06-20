import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

const mockUseAuth = vi.fn();
vi.mock('../../core/context/useAuth', () => ({
  useAuth: () => mockUseAuth(),
}));

import Sidebar from '../../shared/components/layout/Sidebar';

describe('Sidebar', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  it('muestra enlaces para ADMIN', () => {
    mockUseAuth.mockReturnValue({ usuario: { rol: 'ADMIN' }, token: 'x' });
    render(<MemoryRouter><Sidebar /></MemoryRouter>);
    expect(screen.getByText('Dashboard')).toBeInTheDocument();
    expect(screen.getByText('Usuarios')).toBeInTheDocument();
  });

  it('muestra enlaces para DOCENTE', () => {
    mockUseAuth.mockReturnValue({ usuario: { rol: 'DOCENTE' }, token: 'x' });
    render(<MemoryRouter><Sidebar /></MemoryRouter>);
    expect(screen.getByText('Calificaciones')).toBeInTheDocument();
    expect(screen.queryByText('Usuarios')).not.toBeInTheDocument();
    expect(screen.queryByText('Dashboard')).not.toBeInTheDocument();
  });

  it('muestra enlaces para ESTUDIANTE', () => {
    mockUseAuth.mockReturnValue({ usuario: { rol: 'ESTUDIANTE' }, token: 'x' });
    render(<MemoryRouter><Sidebar /></MemoryRouter>);
    expect(screen.getByText('Mis Calificaciones')).toBeInTheDocument();
    expect(screen.queryByText('Usuarios')).not.toBeInTheDocument();
  });

  it('muestra enlaces para APODERADO', () => {
    mockUseAuth.mockReturnValue({ usuario: { rol: 'APODERADO' }, token: 'x' });
    render(<MemoryRouter><Sidebar /></MemoryRouter>);
    expect(screen.getByText('Comunicaciones')).toBeInTheDocument();
    expect(screen.getByText('Justificar')).toBeInTheDocument();
  });

  it('no muestra enlaces cuando no hay rol', () => {
    mockUseAuth.mockReturnValue({ usuario: null, token: null });
    render(<MemoryRouter><Sidebar /></MemoryRouter>);
    expect(screen.queryByText('Dashboard')).not.toBeInTheDocument();
    expect(screen.queryByText('Calificaciones')).not.toBeInTheDocument();
  });
});
