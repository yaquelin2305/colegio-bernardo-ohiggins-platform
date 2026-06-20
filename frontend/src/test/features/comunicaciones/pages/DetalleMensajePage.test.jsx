import { describe, it, expect, vi } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import { BrowserRouter, MemoryRouter, Route, Routes } from 'react-router-dom';
import DetalleMensajePage from '../../../../features/comunicaciones/pages/DetalleMensajePage';
import * as comunicacionesService from '../../../../features/comunicaciones/services/comunicacionesService';

vi.mock('../../../../features/comunicaciones/services/comunicacionesService');
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return { ...actual, useOutletContext: () => ({ setTitulo: vi.fn() }) };
});

function renderPage(mensajeId = '1') {
  return render(
    <MemoryRouter initialEntries={[`/comunicaciones/${mensajeId}`]}>
      <Routes>
        <Route path="/comunicaciones/:id" element={<DetalleMensajePage />} />
      </Routes>
    </MemoryRouter>
  );
}

describe('DetalleMensajePage', () => {
  it('muestra "Cargando..." mientras carga', () => {
    vi.mocked(comunicacionesService.obtenerMensajePorId).mockReturnValue(new Promise(() => {}));
    renderPage();
    expect(screen.getByText('Cargando...')).toBeInTheDocument();
  });

  it('muestra asunto y cuerpo cuando carga éxito', async () => {
    vi.mocked(comunicacionesService.obtenerMensajePorId).mockResolvedValue({
      id: 1, asunto: 'Reunión', mensaje: 'Cuerpo del mensaje',
      remitente: 'Juan', tipo: 'Consulta', canal: 'EMAIL', fecha: '2026-06-15T12:00:00Z', leido: true,
    });
    vi.mocked(comunicacionesService.marcarLeido).mockResolvedValue({});
    renderPage();
    await waitFor(() => expect(screen.getByText('Reunión')).toBeInTheDocument());
    expect(screen.getByText('Cuerpo del mensaje')).toBeInTheDocument();
  });

  it('muestra error si falla la carga', async () => {
    vi.mocked(comunicacionesService.obtenerMensajePorId).mockRejectedValue(new Error('fail'));
    renderPage();
    await waitFor(() => expect(screen.getByText('No se pudo cargar el mensaje.')).toBeInTheDocument());
  });
});
