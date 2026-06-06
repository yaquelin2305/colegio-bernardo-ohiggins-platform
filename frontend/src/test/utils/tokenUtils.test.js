import { describe, it, expect, beforeEach, afterEach } from 'vitest';
import { getUuidFromToken, getPupiloUuidFromToken } from '../../shared/utils/tokenUtils';
import { buildFakeJwt } from '../helpers/jwtFake';

const TOKEN_KEY = 'token';

beforeEach(() => localStorage.clear());
afterEach(() => localStorage.clear());

describe('getUuidFromToken', () => {
  it('retorna userId cuando el token lo tiene', () => {
    const jwt = buildFakeJwt({ userId: 'uuid-123', sub: 'rut-456' });
    localStorage.setItem(TOKEN_KEY, jwt);
    expect(getUuidFromToken()).toBe('uuid-123');
  });

  it('cae a sub cuando no hay userId', () => {
    const jwt = buildFakeJwt({ sub: 'rut-456' });
    localStorage.setItem(TOKEN_KEY, jwt);
    expect(getUuidFromToken()).toBe('rut-456');
  });

  it('retorna null si no hay token en localStorage', () => {
    expect(getUuidFromToken()).toBeNull();
  });

  it('retorna null si el token es malformado', () => {
    localStorage.setItem(TOKEN_KEY, 'esto.no.es.un.jwt.valido');
    expect(getUuidFromToken()).toBeNull();
  });

  it('retorna null si el token tiene solo un segmento', () => {
    localStorage.setItem(TOKEN_KEY, 'solounsegmento');
    expect(getUuidFromToken()).toBeNull();
  });
});

describe('getPupiloUuidFromToken', () => {
  it('retorna pupiloUuid cuando el token lo tiene', () => {
    const jwt = buildFakeJwt({ pupiloUuid: 'pupilo-789' });
    localStorage.setItem(TOKEN_KEY, jwt);
    expect(getPupiloUuidFromToken()).toBe('pupilo-789');
  });

  it('retorna null cuando el token no tiene pupiloUuid', () => {
    const jwt = buildFakeJwt({ userId: 'uuid-123' });
    localStorage.setItem(TOKEN_KEY, jwt);
    expect(getPupiloUuidFromToken()).toBeNull();
  });

  it('retorna null si no hay token en localStorage', () => {
    expect(getPupiloUuidFromToken()).toBeNull();
  });
});
