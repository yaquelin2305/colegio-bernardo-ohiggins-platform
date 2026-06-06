import { describe, it, expect } from 'vitest';
import { renderHook } from '@testing-library/react';
import { useAuth } from '../../core/context/useAuth';
import { AuthProvider } from '../../core/context/AuthContext';

const wrapper = ({ children }) => <AuthProvider>{children}</AuthProvider>;

describe('useAuth', () => {
  it('retorna el contexto sin error cuando está dentro del AuthProvider', () => {
    const { result } = renderHook(() => useAuth(), { wrapper });
    expect(result.current).toHaveProperty('usuario');
    expect(result.current).toHaveProperty('token');
    expect(result.current).toHaveProperty('login');
    expect(result.current).toHaveProperty('logout');
  });

  it('lanza error cuando se usa fuera del AuthProvider', () => {
    expect(() => renderHook(() => useAuth())).toThrowError(
      'useAuth debe usarse dentro de un AuthProvider'
    );
  });
});
