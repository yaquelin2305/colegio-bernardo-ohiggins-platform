import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import LoginForm from '../../../features/auth/components/LoginForm';
import { AuthProvider } from '../../../core/context/AuthContext.jsx';

function base64url(obj) {
  const bytes = new TextEncoder().encode(JSON.stringify(obj));
  let bin = '';
  bytes.forEach(b => { bin += String.fromCharCode(b); });
  return btoa(bin)
    .replace(/\+/g, '-')
    .replace(/\//g, '_')
    .replace(/=+$/, '');
}

function buildFakeJwt(payload) {
  const header = base64url({ alg: 'HS256', typ: 'JWT' });
  const body = base64url(payload);
  return `${header}.${body}.fakesignature`;
}

vi.mock('../../../features/auth/services/authService');
import * as authService from '../../../features/auth/services/authService';

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async (importOriginal) => {
  const actual = await importOriginal();
  return { ...actual, useNavigate: () => mockNavigate };
});

const wrapper = ({ children }) => (
  <MemoryRouter>
    <AuthProvider>{children}</AuthProvider>
  </MemoryRouter>
);

beforeEach(() => {
  vi.clearAllMocks();
  localStorage.clear();
});

function llenarYEnviar(rut = '12345678-9', password = 'Admin1234!') {
  fireEvent.change(screen.getByPlaceholderText('12345678-9'), { target: { value: rut } });
  fireEvent.change(screen.getByPlaceholderText('••••••••'),   { target: { value: password } });
  fireEvent.click(screen.getByRole('button', { name: /Entrar|Iniciando/i }));
}

describe('LoginForm — validación', () => {
  it('muestra error si ambos campos están vacíos', async () => {
    render(<LoginForm />, { wrapper });
    fireEvent.click(screen.getByRole('button', { name: 'Entrar' }));
    expect(await screen.findByText('Por favor ingresa tu RUT y contraseña.')).toBeInTheDocument();
  });
});

describe('LoginForm — redirección por rol', () => {
  it('navega a /dashboard cuando el rol es ADMIN', async () => {
    authService.login.mockResolvedValue(buildFakeJwt({ role: 'ADMIN', nombre: 'Admin' }));
    render(<LoginForm />, { wrapper });
    llenarYEnviar();
    await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith('/dashboard', { replace: true }));
  });
});

describe('LoginForm — manejo de errores del servidor', () => {
  it('muestra el mensaje de error del servidor', async () => {
    authService.login.mockRejectedValue({ response: { data: { mensaje: 'Credenciales inválidas' } } });
    render(<LoginForm />, { wrapper });
    llenarYEnviar('12345678-9', 'wrong');
    expect(await screen.findByText('Credenciales inválidas')).toBeInTheDocument();
  });
});
