import { describe, it, expect } from 'vitest';
import { formatearFecha } from '../../shared/utils/fechaUtils';

describe('formatearFecha', () => {
  it('retorna "—" para null', () => {
    expect(formatearFecha(null)).toBe('—');
  });

  it('retorna "—" para undefined', () => {
    expect(formatearFecha(undefined)).toBe('—');
  });

  it('retorna "—" para string vacío', () => {
    expect(formatearFecha('')).toBe('—');
  });

  it('retorna el string original si no es fecha válida', () => {
    expect(formatearFecha('no-es-fecha')).toBe('no-es-fecha');
  });

  it('formatea correctamente un ISO con Z', () => {
    const resultado = formatearFecha('2024-06-15T10:30:00Z');
    expect(resultado).toMatch(/jun/i);
    expect(resultado).toMatch(/2024/);
  });

  it('formatea correctamente un ISO sin zona horaria (le agrega Z)', () => {
    const resultado = formatearFecha('2024-06-15T10:30:00');
    expect(resultado).toMatch(/jun/i);
    expect(resultado).toMatch(/2024/);
  });

  it('formatea correctamente un ISO con offset +HH:MM', () => {
    const resultado = formatearFecha('2024-06-15T10:30:00-04:00');
    expect(resultado).toMatch(/jun/i);
    expect(resultado).toMatch(/2024/);
  });

  it('no duplica la Z si ya tiene sufijo Z', () => {
    const resultado = formatearFecha('2024-01-01T00:00:00Z');
    expect(typeof resultado).toBe('string');
    expect(resultado).not.toContain('ZZ');
  });
});
