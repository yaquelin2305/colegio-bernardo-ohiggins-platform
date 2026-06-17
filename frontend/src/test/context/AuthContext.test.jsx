import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { AuthProvider } from '../../core/context/AuthContext.jsx';
import { useAuth } from '../../core/context/useAuth';
import { buildFakeJwt } from '../helpers/jwtFake';

const TOKEN_KEY = 'token';
const wrapper = ({ children }) => <AuthProvider>{children}</AuthProvider>;

function buildToken(overrides = {}) {
  return buildFakeJwt({
    userId: 'uuid-001',
    sub: '12345678-9',
    role: 'ADMIN',
    nombre: 'Juan Pérez',
    ...overrides,
  });
}

beforeEach(() => localStorage.clear());
afterEach(() => {
  localStorage.clear();
  vi.restoreAllMocks();
});

describe('AuthProvider — estado inicial', () => {
  it('usuario y token son null cuando no hay token en localStorage', () => {
    const { result } = renderHook(() => useAuth(), { wrapper });
    expect(result.current.usuario).toBeNull();
    expect(result.current.token).toBeNull();
  });

  it('decodifica el token del localStorage al montar', () => {
    const jwt = buildToken();
    localStorage.setItem(TOKEN_KEY, jwt);
    const { result } = renderHook(() => useAuth(), { wrapper });
    expect(result.current.token).toBe(jwt);
    expect(result.current.usuario?.nombre).toBe('Juan Pérez');
    expect(result.current.usuario?.rol).toBe('ADMIN');
  });
});

describe('AuthProvider — login', () => {
  it('guarda el token en localStorage', () => {
    const { result } = renderHook(() => useAuth(), { wrapper });
    const jwt = buildToken();
    act(() => result.current.login(jwt));
    expect(localStorage.getItem(TOKEN_KEY)).toBe(jwt);
  });

  it('actualiza token en el contexto', () => {
    const { result } = renderHook(() => useAuth(), { wrapper });
    const jwt = buildToken();
    act(() => result.current.login(jwt));
    expect(result.current.token).toBe(jwt);
  });

  it('actualiza usuario con los datos del token', () => {
    const { result } = renderHook(() => useAuth(), { wrapper });
    const jwt = buildToken({ nombre: 'María López', role: 'DOCENTE' });
    act(() => result.current.login(jwt));
    expect(result.current.usuario?.nombre).toBe('María López');
    expect(result.current.usuario?.rol).toBe('DOCENTE');
  });
});

describe('AuthProvider — logout', () => {
  it('elimina el token de localStorage', () => {
    const jwt = buildToken();
    localStorage.setItem(TOKEN_KEY, jwt);
    vi.stubGlobal('location', { href: '' });

    const { result } = renderHook(() => useAuth(), { wrapper });
    act(() => result.current.logout());
    expect(localStorage.getItem(TOKEN_KEY)).toBeNull();
  });

  it('pone usuario y token en null', () => {
    const jwt = buildToken();
    localStorage.setItem(TOKEN_KEY, jwt);
    vi.stubGlobal('location', { href: '' });

    const { result } = renderHook(() => useAuth(), { wrapper });
    act(() => result.current.logout());
    expect(result.current.token).toBeNull();
    expect(result.current.usuario).toBeNull();
  });
});
