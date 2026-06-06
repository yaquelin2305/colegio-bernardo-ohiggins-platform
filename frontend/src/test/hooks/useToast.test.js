import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { useToast } from '../../shared/hooks/useToast';

beforeEach(() => vi.useFakeTimers());
afterEach(() => vi.useRealTimers());

describe('useToast', () => {
  it('estado inicial: toast es null', () => {
    const { result } = renderHook(() => useToast());
    expect(result.current.toast).toBeNull();
  });

  it('showToast establece mensaje y tipo success', () => {
    const { result } = renderHook(() => useToast());
    act(() => result.current.showToast('Guardado correctamente', 'success'));
    expect(result.current.toast).toEqual({ message: 'Guardado correctamente', type: 'success' });
  });

  it('showToast usa success como tipo por defecto', () => {
    const { result } = renderHook(() => useToast());
    act(() => result.current.showToast('Mensaje sin tipo'));
    expect(result.current.toast?.type).toBe('success');
  });

  it('showToast con tipo error', () => {
    const { result } = renderHook(() => useToast());
    act(() => result.current.showToast('Algo falló', 'error'));
    expect(result.current.toast).toEqual({ message: 'Algo falló', type: 'error' });
  });

  it('toast vuelve a null después de 3500ms', () => {
    const { result } = renderHook(() => useToast());
    act(() => result.current.showToast('Temporal'));
    expect(result.current.toast).not.toBeNull();
    act(() => vi.advanceTimersByTime(3500));
    expect(result.current.toast).toBeNull();
  });

  it('toast sigue visible antes de los 3500ms', () => {
    const { result } = renderHook(() => useToast());
    act(() => result.current.showToast('Visible'));
    act(() => vi.advanceTimersByTime(3000));
    expect(result.current.toast).not.toBeNull();
  });
});
