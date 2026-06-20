import { describe, it, expect, vi } from 'vitest';
import {
  obtenerMensajes,
  obtenerMensajePorId,
  enviarMensaje,
  obtenerDestinatarios,
  marcarLeido,
} from '../../../../features/comunicaciones/services/comunicacionesService';
import axiosClient from '../../../../core/api/axiosClient';

vi.mock('../../../../core/api/axiosClient');

describe('comunicacionesService', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  describe('obtenerMensajes', () => {
    it('hace GET a /bff/comunicaciones/bandeja/{usuarioId}', async () => {
      const mensajes = [{ id: 1, asunto: 'Test' }];
      vi.mocked(axiosClient.get).mockResolvedValue({ data: mensajes });
      const result = await obtenerMensajes('u-456');
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/comunicaciones/bandeja/u-456');
      expect(result).toEqual(mensajes);
    });
  });

  describe('obtenerMensajePorId', () => {
    it('hace GET a /bff/comunicaciones/{id}', async () => {
      const mensaje = { id: 7, asunto: 'Reunión' };
      vi.mocked(axiosClient.get).mockResolvedValue({ data: mensaje });
      const result = await obtenerMensajePorId(7);
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/comunicaciones/7');
      expect(result).toEqual(mensaje);
    });
  });

  describe('enviarMensaje', () => {
    it('hace POST con payload destructured', async () => {
      const payload = { destinatario: 'd1', asunto: 'Hola', mensaje: 'Cuerpo', canal: 'EMAIL', tipo: 'CONSULTA' };
      vi.mocked(axiosClient.post).mockResolvedValue({ data: { ok: true } });
      const result = await enviarMensaje(payload);
      expect(axiosClient.post).toHaveBeenCalledWith('/bff/comunicaciones/enviar', payload);
      expect(result).toEqual({ ok: true });
    });
  });

  describe('obtenerDestinatarios', () => {
    it('hace GET a /bff/comunicaciones/destinatarios', async () => {
      const destinatarios = [{ id: 'd1', nombre: 'Juan' }];
      vi.mocked(axiosClient.get).mockResolvedValue({ data: destinatarios });
      const result = await obtenerDestinatarios();
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/comunicaciones/destinatarios');
      expect(result).toEqual(destinatarios);
    });
  });

  describe('marcarLeido', () => {
    it('hace PATCH a /bff/comunicaciones/leido/{id}', async () => {
      vi.mocked(axiosClient.patch).mockResolvedValue({ data: { leido: true } });
      const result = await marcarLeido(42);
      expect(axiosClient.patch).toHaveBeenCalledWith('/bff/comunicaciones/leido/42');
      expect(result).toEqual({ leido: true });
    });
  });
});
