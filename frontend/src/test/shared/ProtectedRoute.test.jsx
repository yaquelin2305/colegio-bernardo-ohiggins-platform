import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import ProtectedRoute from '../../shared/components/layout/ProtectedRoute';
import { AuthProvider } from '../../core/context/AuthContext';
import { buildFakeJwt } from '../helpers/jwtFake';

const TOKEN_KEY = 'token';

beforeEach(() => localStorage.clear());
afterEach(() => localStorage.clear());

function renderRuta({ roles, token } = {}) {
  if (token) localStorage.setItem(TOKEN_KEY, token);

  return render(
    <MemoryRouter initialEntries={['/protegida']}>
      <AuthProvider>
        <Routes>
          <Route
            path="/protegida"
            element={
              <ProtectedRoute allowedRoles={roles}>
                <div>Contenido protegido</div>
              </ProtectedRoute>
            }
          />
          <Route path="/login"              element={<div>Página de login</div>} />
          <Route path="/mis-calificaciones" element={<div>Mis calificaciones</div>} />
          <Route path="/calificaciones"     element={<div>Calificaciones</div>} />
        </Routes>
      </AuthProvider>
    </MemoryRouter>
  );
}

describe('ProtectedRoute', () => {
  it('redirige a /login cuando no hay token', () => {
    renderRuta();
    expect(screen.getByText('Página de login')).toBeInTheDocument();
  });

  it('muestra el contenido cuando hay token y no hay restricción de roles', () => {
    renderRuta({ token: buildFakeJwt({ role: 'ADMIN', nombre: 'Admin' }) });
    expect(screen.getByText('Contenido protegido')).toBeInTheDocument();
  });

  it('muestra el contenido cuando el rol del usuario está permitido', () => {
    renderRuta({
      token: buildFakeJwt({ role: 'DOCENTE', nombre: 'Docente' }),
      roles: ['ADMIN', 'DOCENTE'],
    });
    expect(screen.getByText('Contenido protegido')).toBeInTheDocument();
  });

  it('redirige a /mis-calificaciones cuando el rol APODERADO no está permitido', () => {
    renderRuta({
      token: buildFakeJwt({ role: 'APODERADO', nombre: 'Apoderado' }),
      roles: ['ADMIN'],
    });
    expect(screen.getByText('Mis calificaciones')).toBeInTheDocument();
  });

  it('redirige a /mis-calificaciones cuando el rol ESTUDIANTE no está permitido', () => {
    renderRuta({
      token: buildFakeJwt({ role: 'ESTUDIANTE', nombre: 'Estudiante' }),
      roles: ['ADMIN', 'DOCENTE'],
    });
    expect(screen.getByText('Mis calificaciones')).toBeInTheDocument();
  });

  it('redirige a /calificaciones cuando el rol DOCENTE no está en la lista permitida', () => {
    renderRuta({
      token: buildFakeJwt({ role: 'DOCENTE', nombre: 'Docente' }),
      roles: ['ADMIN'],
    });
    expect(screen.getByText('Calificaciones')).toBeInTheDocument();
  });
});
