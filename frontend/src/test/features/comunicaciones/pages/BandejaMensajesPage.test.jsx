import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import BandejaMensajesPage from '../../../../features/comunicaciones/pages/BandejaMensajesPage';
import * as comunicacionesService from '../../../../features/comunicaciones/services/comunicacionesService';
import { useAuth } from '../../../../core/context/useAuth';

vi.mock('../../../../core/context/useAuth');
vi.mock('../../../../features/comunicaciones/services/comunicacionesService');
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useOutletContext: () => ({ setTitulo: vi.fn() }) };
});

function renderPage() {
  return render(<BrowserRouter><BandejaMensajesPage /></BrowserRouter>);
}

describe('BandejaMensajesPage', () => {
  beforeEach(() => { vi.clearAllMocks(); });
  it('muestra "Cargando..." mientras obtiene mensajes', () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { userId: 'u1' }, logout: vi.fn() });
    vi.mocked(comunicacionesService.obtenerMensajes).mockReturnValue(new Promise(() => {}));
    renderPage();
    expect(screen.getByText('Cargando...')).toBeInTheDocument();
  });

  it('muestra mensajes después de carga exitosa', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { userId: 'u1' }, logout: vi.fn() });
    vi.mocked(comunicacionesService.obtenerMensajes).mockResolvedValue([
      { id: 1, asunto: 'Test', leido: false },
    ]);
    renderPage();
    await waitFor(() => expect(screen.getByText('Test')).toBeInTheDocument());
  });

  it('muestra error si falla la carga', async () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: { userId: 'u1' }, logout: vi.fn() });
    vi.mocked(comunicacionesService.obtenerMensajes).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('No se pudo cargar la bandeja de mensajes.')).toBeInTheDocument());
  });

  it('no llama obtenerMensajes si usuario es null', () => {
    vi.mocked(useAuth).mockReturnValue({ usuario: null, logout: vi.fn() });
    renderPage();
    expect(comunicacionesService.obtenerMensajes).not.toHaveBeenCalled();
  });
});
