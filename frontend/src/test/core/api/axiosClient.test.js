import { describe, it, expect, vi, beforeEach } from 'vitest';
import MockAdapter from 'axios-mock-adapter';
import axiosClient from '../../../core/api/axiosClient';

const mock = new MockAdapter(axiosClient);

describe('axiosClient', () => {
  beforeEach(() => {
    mock.reset();
    localStorage.clear();
  });

  it('agrega Authorization header cuando hay token en localStorage', async () => {
    localStorage.setItem('token', 'mi-jwt');
    let capturedHeaders;
    mock.onGet('/test-auth').reply(config => {
      capturedHeaders = config.headers;
      return [200, {}];
    });
    await axiosClient.get('/test-auth');
    expect(capturedHeaders.Authorization).toBe('Bearer mi-jwt');
  });

  it('no agrega header cuando no hay token', async () => {
    let capturedHeaders;
    mock.onGet('/test-no-token').reply(config => {
      capturedHeaders = config.headers;
      return [200, {}];
    });
    await axiosClient.get('/test-no-token');
    expect(capturedHeaders.Authorization).toBeUndefined();
  });

  it('limpia token y lanza error en 401', async () => {
    localStorage.setItem('token', 'expirado');
    mock.onGet('/test-401').reply(401);
    await expect(axiosClient.get('/test-401')).rejects.toThrow('Request failed with status code 401');
    expect(localStorage.getItem('token')).toBeNull();
  });

  it('NO redirige ni limpia token en errores que no son 401', async () => {
    localStorage.setItem('token', 'valido');
    mock.onGet('/test-500').reply(500);
    await expect(axiosClient.get('/test-500')).rejects.toThrow('Request failed with status code 500');
    expect(localStorage.getItem('token')).toBe('valido');
  });

  it('usa el Content-Type application/json', () => {
    expect(axiosClient.defaults.headers['Content-Type']).toBe('application/json');
  });
});
