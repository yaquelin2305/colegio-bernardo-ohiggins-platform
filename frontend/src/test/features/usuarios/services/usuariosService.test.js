import { describe, it, expect, vi } from 'vitest';
import {
  obtenerDocentes, obtenerApoderados, obtenerEstudiantes,
  crearUsuario, actualizarUsuario, eliminarUsuario,
} from '../../../../features/usuarios/services/usuariosService';
import axiosClient from '../../../../core/api/axiosClient';

vi.mock('../../../../core/api/axiosClient');

const DOCENTE_API = { id: 'd1', rut: '11.111.111-1', nombreCompleto: 'Ana Profe', rol: 'DOCENTE' };
const APODERADO_API = { id: 'a1', rut: '22.222.222-2', nombreCompleto: 'Pedro Apoderado', rol: 'APODERADO', pupiloUuid: 'p-1', pupiloNombre: 'Hijo' };

describe('usuariosService', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  describe('obtenerDocentes', () => {
    it('llama GET /bff/usuarios/DOCENTE y mapea respuesta', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({ data: [DOCENTE_API] });
      const result = await obtenerDocentes();
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/usuarios/DOCENTE');
      expect(result).toEqual([{
        id: 'd1', rut: '11.111.111-1', nombres: 'Ana', apellidos: 'Profe',
        email: '', rol: 'DOCENTE', pupiloUuid: null, pupiloNombre: null,
      }]);
    });
  });

  describe('obtenerApoderados', () => {
    it('llama GET /bff/usuarios/APODERADO y mapea incluyendo pupilo', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({ data: [APODERADO_API] });
      const result = await obtenerApoderados();
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/usuarios/APODERADO');
      expect(result).toEqual([{
        id: 'a1', rut: '22.222.222-2', nombres: 'Pedro', apellidos: 'Apoderado',
        email: '', rol: 'APODERADO', pupiloUuid: 'p-1', pupiloNombre: 'Hijo',
      }]);
    });
  });

  describe('obtenerEstudiantes', () => {
    it('llama GET /bff/usuarios/ESTUDIANTE', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({ data: [] });
      const result = await obtenerEstudiantes();
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/usuarios/ESTUDIANTE');
      expect(result).toEqual([]);
    });
  });

  describe('mapeoUsuario', () => {
    it('maneja nombreCompleto con una sola palabra', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({ data: [{ id: 'x', nombreCompleto: 'SoloNombre', rol: 'DOCENTE' }] });
      const [result] = await obtenerDocentes();
      expect(result.nombres).toBe('SoloNombre');
      expect(result.apellidos).toBe('');
    });

    it('maneja nombreCompleto undefined', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({ data: [{ id: 'x', rol: 'DOCENTE' }] });
      const [result] = await obtenerDocentes();
      expect(result.nombres).toBe('');
      expect(result.apellidos).toBe('');
    });
  });

  describe('crearUsuario', () => {
    it('hace POST a /v1/admin/crear y mapea respuesta', async () => {
      vi.mocked(axiosClient.post).mockResolvedValue({ data: { ...DOCENTE_API, id: 'nuevo' } });
      const payload = { rut: '11.111.111-1', nombres: 'Ana', apellidos: 'Profe', email: 'a@b.cl', password: '123', rol: 'DOCENTE', pupiloUuid: undefined };
      const result = await crearUsuario(payload);
      expect(axiosClient.post).toHaveBeenCalledWith('/v1/admin/crear', {
        rut: '11.111.111-1', nombre: 'Ana', apellido: 'Profe',
        email: 'a@b.cl', password: '123', rol: 'DOCENTE', pupiloUuid: null,
      });
      expect(result.id).toBe('nuevo');
    });
  });

  describe('actualizarUsuario', () => {
    it('hace PUT a /v1/admin/actualizar/{id}', async () => {
      vi.mocked(axiosClient.put).mockResolvedValue({ data: DOCENTE_API });
      await actualizarUsuario('d1', { nombres: 'Ana', apellidos: 'Profe', email: 'a@b.cl' });
      expect(axiosClient.put).toHaveBeenCalledWith('/v1/admin/actualizar/d1', {
        nombre: 'Ana', apellido: 'Profe', email: 'a@b.cl',
      });
    });
  });

  describe('eliminarUsuario', () => {
    it('hace DELETE a /v1/admin/eliminar/{id}', async () => {
      vi.mocked(axiosClient.delete).mockResolvedValue({});
      await eliminarUsuario('d1');
      expect(axiosClient.delete).toHaveBeenCalledWith('/v1/admin/eliminar/d1');
    });
  });
});
