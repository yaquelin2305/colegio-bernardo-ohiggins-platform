import { describe, it, expect, vi } from 'vitest';
import {
  obtenerResumenDiario,
  obtenerAsistenciaPorCurso,
  obtenerCursos,
  obtenerAlumnosPorCurso,
  obtenerAlumnosPorDocente,
  obtenerAlumnos,
  obtenerHistorialAsistencia,
  obtenerInasistencias,
  justificarInasistencia,
  guardarAnotacion,
  obtenerAnotacionesPorEstudiante,
  guardarAsistencia,
} from '../../../../features/asistencia/services/asistenciaService';
import axiosClient from '../../../../core/api/axiosClient';

vi.mock('../../../../core/api/axiosClient');

describe('asistenciaService', () => {
  beforeEach(() => { vi.clearAllMocks(); });

  describe('obtenerResumenDiario', () => {
    it('hace GET con params cursoId y fecha', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({ data: { presentes: 20 } });
      const result = await obtenerResumenDiario(1, '2026-06-15');
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/asistencia/resumen', {
        params: { cursoId: 1, fecha: '2026-06-15' },
      });
      expect(result).toEqual({ presentes: 20 });
    });

    it('retorna null cuando cursoId es falsy', async () => {
      const result = await obtenerResumenDiario(null, '2026-06-15');
      expect(result).toBeNull();
      expect(axiosClient.get).not.toHaveBeenCalled();
    });
  });

  describe('obtenerAsistenciaPorCurso', () => {
    it('hace GET con cursoId en URL y fecha como query param', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({ data: [{ estudianteId: 1, estado: 'PRESENTE' }] });
      const result = await obtenerAsistenciaPorCurso(5, '2026-06-10');
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/asistencia/curso/5', {
        params: { fecha: '2026-06-10' },
      });
      expect(result).toHaveLength(1);
    });
  });

  describe('obtenerCursos', () => {
    it('hace GET a /bff/cursos', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({ data: [{ id: 1, nombre: '1°A' }] });
      const result = await obtenerCursos();
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/cursos');
      expect(result).toHaveLength(1);
    });
  });

  describe('obtenerAlumnosPorCurso', () => {
    it('mapea nombres compuestos separando nombre y apellido', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({
        data: [{ estudianteId: 'e1', rut: '11-1', nombre: 'Juan Carlos Pérez' }],
      });
      const result = await obtenerAlumnosPorCurso(1);
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/asistencia/alumnos/1');
      expect(result).toEqual([{ id: 'e1', rut: '11-1', nombre: 'Juan', apellido: 'Carlos Pérez' }]);
    });

    it('usa nombre vacío cuando el campo nombre es string vacío (?? no fallback)', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({
        data: [{ estudianteId: 'e2', rut: '22-2', nombre: '' }],
      });
      const result = await obtenerAlumnosPorCurso(2);
      expect(result[0].nombre).toBe('');
    });
  });

  describe('obtenerAlumnosPorDocente', () => {
    it('obtiene cursos luego alumnos y deduplica', async () => {
      vi.mocked(axiosClient.get)
        .mockResolvedValueOnce({ data: [{ id: 1, nombre: '1°A' }, { id: 2, nombre: '2°B' }] })
        .mockResolvedValueOnce({ data: [{ estudianteId: 'e1', nombre: 'Juan Pérez' }] })
        .mockResolvedValueOnce({ data: [{ estudianteId: 'e1', nombre: 'Juan Pérez' }] });
      const result = await obtenerAlumnosPorDocente();
      expect(result).toHaveLength(1);
      expect(result[0]).toEqual({ id: 'e1', nombre: 'Juan Pérez', rut: '', curso: '1°A' });
    });
  });

  describe('obtenerAlumnos', () => {
    it('mapea desde /bff/usuarios/ESTUDIANTE', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({
        data: [{ id: 'e1', nombreCompleto: 'María López', rut: '11-1' }],
      });
      const result = await obtenerAlumnos();
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/usuarios/ESTUDIANTE');
      expect(result).toEqual([{ id: 'e1', nombre: 'María López', rut: '11-1', curso: '' }]);
    });
  });

  describe('obtenerHistorialAsistencia', () => {
    it('hace GET a /bff/asistencia/estudiante/{alumnoId}', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({ data: { asistencia: [] } });
      const result = await obtenerHistorialAsistencia('e1');
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/asistencia/estudiante/e1');
      expect(result).toEqual({ asistencia: [] });
    });
  });

  describe('obtenerInasistencias', () => {
    it('hace GET a /bff/asistencia/inasistencias', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({ data: [{ id: 1 }] });
      const result = await obtenerInasistencias();
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/asistencia/inasistencias');
      expect(result).toHaveLength(1);
    });
  });

  describe('justificarInasistencia', () => {
    it('hace PATCH con motivo en el body', async () => {
      vi.mocked(axiosClient.patch).mockResolvedValue({ data: { justificado: true } });
      const result = await justificarInasistencia(99, { motivo: 'Enfermedad' });
      expect(axiosClient.patch).toHaveBeenCalledWith('/bff/asistencia/99/justificar', { motivo: 'Enfermedad' });
      expect(result).toEqual({ justificado: true });
    });
  });

  describe('guardarAnotacion', () => {
    it('hace POST con estudianteId, tipo y descripcion', async () => {
      vi.mocked(axiosClient.post).mockResolvedValue({ data: { id: 1 } });
      const result = await guardarAnotacion('e1', { tipo: 'POSITIVA', descripcion: 'Buen trabajo' });
      expect(axiosClient.post).toHaveBeenCalledWith('/bff/asistencia/anotaciones', {
        estudianteId: 'e1', tipo: 'POSITIVA', descripcion: 'Buen trabajo',
      });
      expect(result).toEqual({ id: 1 });
    });
  });

  describe('obtenerAnotacionesPorEstudiante', () => {
    it('hace GET a /bff/asistencia/anotaciones/estudiante/{id}', async () => {
      vi.mocked(axiosClient.get).mockResolvedValue({ data: [{ tipo: 'POSITIVA' }] });
      const result = await obtenerAnotacionesPorEstudiante('e1');
      expect(axiosClient.get).toHaveBeenCalledWith('/bff/asistencia/anotaciones/estudiante/e1');
      expect(result).toHaveLength(1);
    });
  });

  describe('guardarAsistencia', () => {
    it('mapea listado y hace POST', async () => {
      vi.mocked(axiosClient.post).mockResolvedValue({ data: { registrados: 2 } });
      const result = await guardarAsistencia(1, '2026-06-15', [
        { estudianteId: 'e1', estado: 'presente' },
        { nombre: 'e2', estado: 'ausente' },
      ]);
      expect(axiosClient.post).toHaveBeenCalledWith('/bff/asistencia/registrar', [
        { estudianteId: 'e1', cursoId: 1, estado: 'PRESENTE', observacion: null, fecha: '2026-06-15' },
        { estudianteId: 'e2', cursoId: 1, estado: 'AUSENTE', observacion: null, fecha: '2026-06-15' },
      ]);
      expect(result).toEqual({ registrados: 2 });
    });
  });
});
