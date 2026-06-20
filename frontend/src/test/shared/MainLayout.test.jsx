import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import MainLayout from '../../shared/components/layout/MainLayout';

vi.mock('../../core/context/useAuth', () => ({
  useAuth: () => ({
    usuario: { nombre: 'Admin', rol: 'ADMIN' },
    token: 'x',
    logout: vi.fn(),
  }),
}));

describe('MainLayout', () => {
  it('renderiza Sidebar, Header y Outlet', () => {
    render(
      <MemoryRouter initialEntries={['/']}>
        <Routes>
          <Route path="/" element={<MainLayout />}>
            <Route index element={<p>Contenido principal</p>} />
          </Route>
        </Routes>
      </MemoryRouter>
    );
    expect(screen.getByText('Contenido principal')).toBeInTheDocument();
    expect(screen.getAllByText('Gestión Académica').length).toBeGreaterThanOrEqual(1);
  });
});
