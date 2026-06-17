import { describe, it, expect, vi, beforeEach } from 'vitest';
import axiosClient from '../../../core/api/axiosClient';
import { login } from '../../../features/auth/services/authService';

vi.mock('../../../core/api/axiosClient');

beforeEach(() => {
  vi.clearAllMocks();
});

describe('authService.login', () => {
  it('retorna el accessToken cuando la petición es exitosa', async () => {
    axiosClient.post.mockResolvedValue({ data: { accessToken: 'jwt-fake' } });
    const token = await login('12345678-9', 'Admin1234!');
    expect(token).toBe('jwt-fake');
  });

  it('llama al endpoint /v1/auth/login con rut y password', async () => {
    axiosClient.post.mockResolvedValue({ data: { accessToken: 'jwt-fake' } });
    await login('12345678-9', 'Admin1234!');
    expect(axiosClient.post).toHaveBeenCalledWith('/v1/auth/login', {
      rut: '12345678-9',
      password: 'Admin1234!',
    });
  });

  it('sanitiza el RUT eliminando puntos y espacios', async () => {
    axiosClient.post.mockResolvedValue({ data: { accessToken: 'jwt-fake' } });
    await login('12.345.678-9', 'pass');
    expect(axiosClient.post).toHaveBeenCalledWith('/v1/auth/login', {
      rut: '12345678-9',
      password: 'pass',
    });
  });

  it('lanza error cuando la petición falla', async () => {
    const error = new Error('Network Error');
    axiosClient.post.mockRejectedValue(error);
    await expect(login('12345678-9', 'wrong')).rejects.toThrow('Network Error');
  });

  it('lanza el error del servidor cuando hay response', async () => {
    const error = { response: { data: { mensaje: 'Credenciales inválidas' } } };
    axiosClient.post.mockRejectedValue(error);
    await expect(login('12345678-9', 'wrong')).rejects.toEqual(error);
  });
});
