import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor, fireEvent } from '@testing-library/react';
import { BrowserRouter, MemoryRouter, Route, Routes } from 'react-router-dom';
import RedactarMensajePage from '../../../../features/comunicaciones/pages/RedactarMensajePage';
import * as comunicacionesService from '../../../../features/comunicaciones/services/comunicacionesService';

vi.mock('../../../../features/comunicaciones/services/comunicacionesService');
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useOutletContext: () => ({ setTitulo: vi.fn() }) };
});

function renderPage() {
  return render(
    <MemoryRouter initialEntries={['/comunicaciones/redactar']}>
      <Routes>
        <Route path="/comunicaciones/redactar" element={<RedactarMensajePage />} />
      </Routes>
    </MemoryRouter>
  );
}

describe('RedactarMensajePage', () => {
  it('carga destinatarios al montar', async () => {
    vi.mocked(comunicacionesService.obtenerDestinatarios).mockResolvedValue([
      { id: 'd1', nombre: 'Juan' },
    ]);
    renderPage();
    await waitFor(() => expect(screen.getByText('Juan')).toBeInTheDocument());
  });

  it('muestra error si falla carga de destinatarios', async () => {
    vi.mocked(comunicacionesService.obtenerDestinatarios).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('No se pudo cargar la lista de destinatarios.')).toBeInTheDocument());
  });

  it('renderiza FormularioMensaje con los campos iniciales', async () => {
    vi.mocked(comunicacionesService.obtenerDestinatarios).mockResolvedValue([]);
    renderPage();
    await waitFor(() => expect(screen.getByLabelText('Asunto')).toBeInTheDocument());
  });

  it('navega a /comunicaciones tras envío exitoso', async () => {
    vi.mocked(comunicacionesService.obtenerDestinatarios).mockResolvedValue([]);
    vi.mocked(comunicacionesService.enviarMensaje).mockResolvedValue({});
    renderPage();
    await waitFor(() => expect(screen.getByLabelText('Asunto')).toBeInTheDocument());
    fireEvent.submit(screen.getByRole('button', { name: /enviar mensaje/i }));
    await waitFor(() => expect(comunicacionesService.enviarMensaje).toHaveBeenCalled());
  });
});
