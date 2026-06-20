import { describe, it, expect, vi } from 'vitest';
import { login } from '../../../../features/auth/services/authService';
import axiosClient from '../../../../core/api/axiosClient';

vi.mock('../../../../core/api/axiosClient');

describe('authService', () => {
  it('sanitiza el RUT eliminando puntos y espacios', async () => {
    vi.mocked(axiosClient.post).mockResolvedValue({ data: { accessToken: 'tk_abc' } });
    const result = await login('12.345.678-9', 'secreta');
    expect(axiosClient.post).toHaveBeenCalledWith('/v1/auth/login', {
      rut: '12345678-9',
      password: 'secreta',
    });
    expect(result).toBe('tk_abc');
  });

  it('funciona con RUT sin puntos ni espacios', async () => {
    vi.mocked(axiosClient.post).mockResolvedValue({ data: { accessToken: 'tk_def' } });
    const result = await login('12345678-9', 'pass');
    expect(axiosClient.post).toHaveBeenCalledWith('/v1/auth/login', {
      rut: '12345678-9',
      password: 'pass',
    });
    expect(result).toBe('tk_def');
  });

  it('propaga el error cuando la API falla', async () => {
    const error = new Error('Credenciales inválidas');
    vi.mocked(axiosClient.post).mockRejectedValue(error);
    await expect(login('12.345.678-9', 'wrong')).rejects.toThrow('Credenciales inválidas');
  });
});
