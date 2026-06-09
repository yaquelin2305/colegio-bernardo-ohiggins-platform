import { describe, it, expect, vi, beforeEach } from 'vitest';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import LoginForm from '../../../features/auth/components/LoginForm';
import { AuthProvider } from '../../../core/context/AuthContext.jsx';
import { buildFakeJwt } from '../../helpers/jwtFake';

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

  it('muestra error si solo la contraseña está vacía', async () => {
    render(<LoginForm />, { wrapper });
    fireEvent.change(screen.getByPlaceholderText('12345678-9'), { target: { value: '12345678-9' } });
    fireEvent.click(screen.getByRole('button', { name: 'Entrar' }));
    expect(await screen.findByText('Por favor ingresa tu RUT y contraseña.')).toBeInTheDocument();
  });

  it('limpia el error al modificar un campo', async () => {
    render(<LoginForm />, { wrapper });
    fireEvent.click(screen.getByRole('button', { name: 'Entrar' }));
    expect(await screen.findByText('Por favor ingresa tu RUT y contraseña.')).toBeInTheDocument();
    fireEvent.change(screen.getByPlaceholderText('12345678-9'), { target: { value: '12345678-9' } });
    expect(screen.queryByText('Por favor ingresa tu RUT y contraseña.')).not.toBeInTheDocument();
  });
});

describe('LoginForm — redirección por rol', () => {
  it('navega a /dashboard cuando el rol es ADMIN', async () => {
    authService.login.mockResolvedValue(buildFakeJwt({ role: 'ADMIN', nombre: 'Admin' }));
    render(<LoginForm />, { wrapper });
    llenarYEnviar();
    await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith('/dashboard', { replace: true }));
  });

  it('navega a /calificaciones cuando el rol es DOCENTE', async () => {
    authService.login.mockResolvedValue(buildFakeJwt({ role: 'DOCENTE', nombre: 'Docente' }));
    render(<LoginForm />, { wrapper });
    llenarYEnviar('11111111-1');
    await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith('/calificaciones', { replace: true }));
  });

  it('navega a /mis-calificaciones cuando el rol es ESTUDIANTE', async () => {
    authService.login.mockResolvedValue(buildFakeJwt({ role: 'ESTUDIANTE', nombre: 'Estudiante' }));
    render(<LoginForm />, { wrapper });
    llenarYEnviar('22222222-2');
    await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith('/mis-calificaciones', { replace: true }));
  });

  it('navega a /mis-calificaciones cuando el rol es APODERADO', async () => {
    authService.login.mockResolvedValue(buildFakeJwt({ role: 'APODERADO', nombre: 'Apoderado' }));
    render(<LoginForm />, { wrapper });
    llenarYEnviar('33333333-3');
    await waitFor(() => expect(mockNavigate).toHaveBeenCalledWith('/mis-calificaciones', { replace: true }));
  });
});

describe('LoginForm — manejo de errores del servidor', () => {
  it('muestra el mensaje de error del servidor', async () => {
    authService.login.mockRejectedValue({ response: { data: { mensaje: 'Credenciales inválidas' } } });
    render(<LoginForm />, { wrapper });
    llenarYEnviar('12345678-9', 'wrong');
    expect(await screen.findByText('Credenciales inválidas')).toBeInTheDocument();
  });

  it('muestra mensaje de error genérico si no hay detalle del servidor', async () => {
    authService.login.mockRejectedValue(new Error('Network Error'));
    render(<LoginForm />, { wrapper });
    llenarYEnviar();
    expect(await screen.findByText('Network Error')).toBeInTheDocument();
  });
});

describe('LoginForm — estado de carga', () => {
  it('deshabilita el botón y cambia texto mientras carga', async () => {
    authService.login.mockReturnValue(new Promise(() => {}));
    render(<LoginForm />, { wrapper });
    llenarYEnviar();
    const boton = await screen.findByRole('button', { name: 'Iniciando sesión...' });
    expect(boton).toBeDisabled();
  });
});

describe('LoginForm — cobertura adicional', () => {
  it('llama a authService.login con el rut y contraseña ingresados', async () => {
    authService.login.mockResolvedValue(buildFakeJwt({ role: 'ADMIN' }));
    render(<LoginForm />, { wrapper });
    llenarYEnviar();
    await waitFor(() => {
      expect(authService.login).toHaveBeenCalledTimes(1);
      expect(authService.login).toHaveBeenCalledWith('12345678-9', 'Admin1234!');
    });
  });

  it('deshabilita ambos campos mientras se realiza la autenticación', async () => {
    authService.login.mockReturnValue(new Promise(() => {}));
    render(<LoginForm />, { wrapper });
    llenarYEnviar();
    const boton = await screen.findByRole('button', { name: 'Iniciando sesión...' });
    expect(boton).toBeDisabled();
    expect(screen.getByPlaceholderText('12345678-9')).toBeDisabled();
    expect(screen.getByPlaceholderText('••••••••')).toBeDisabled();
  });

  it('vuelve a habilitar el formulario cuando ocurre un error de autenticación', async () => {
    authService.login.mockRejectedValue(new Error('Error de autenticación'));
    render(<LoginForm />, { wrapper });
    llenarYEnviar();
    expect(await screen.findByText('Error de autenticación')).toBeInTheDocument();
    expect(screen.getByRole('button', { name: 'Entrar' })).not.toBeDisabled();
    expect(screen.getByPlaceholderText('12345678-9')).not.toBeDisabled();
    expect(screen.getByPlaceholderText('••••••••')).not.toBeDisabled();
  });
});
